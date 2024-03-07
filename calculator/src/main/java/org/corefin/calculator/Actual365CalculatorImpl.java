package org.corefin.calculator;

import org.corefin.calculator.model.Installment;
import org.corefin.calculator.model.Loan;
import org.corefin.model.common.InstallmentStatus;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

// Compute simple interest via the Actual 365 method,
// based on Appendix J of Regulation Z.
// Actual/365 is calculated by taking the annual interest rate and dividing
// it by 365 and then multiplying that number by the amount of days in the current month
// https://www.consumerfinance.gov/rules-policy/regulations/1026/j/
// 1 - First compute the EMI (equated monthly installment) using the reducing balance method
//     The formula for this is:
//     EMI = P * [(r(1 + r) ^ n) / (((1 + r) ^ n) - 1)]
//      where
//      ** r = annual compounding interest rate / number of payments per period
//         For example, a 15% APR (annual) compounding monthly would incur
//         12 payments per year, and thus.
//         r = 0.15 / 12 = 0.0125
//      ** P = principal amount
//      ** n = number of compounding periods over the lifetime of the loan,
//         For example, a 30-year loan paid monthly would have a n value of
//         n = 30 * 12 = 360
public class Actual365CalculatorImpl {

    // rate is annual compounding interest rate / number of payments per period
    public BigDecimal getEquatedMonthlyInstallment(Loan loan) {
        BigDecimal monthlyRate = loan.targetInterestRate().divide(new BigDecimal(12),
                10,
                RoundingMode.HALF_UP);
        // If it's a 0% interest rate, then just do originated loan amount / number of terms
        if (loan.targetInterestRate().compareTo(BigDecimal.ZERO) == 0) {
            return loan.originatedAmount().divide(new BigDecimal(loan.term()), 2, RoundingMode.HALF_UP);
        }
        return monthlyRate.multiply(monthlyRate.add(BigDecimal.ONE).pow(loan.term())).divide(
                (monthlyRate.add(BigDecimal.ONE).pow(loan.term())).subtract(BigDecimal.ONE),
                10,
                RoundingMode.HALF_UP
        ).multiply(loan.originatedAmount()).setScale(2, RoundingMode.HALF_UP);
    }

    public List<Installment> getActual365AmortizationSchedule(Loan loan) {
        List<Installment> installments = new ArrayList<>();
        BigDecimal installmentAmount = getEquatedMonthlyInstallment(loan);
        BigDecimal outstandingPrincipal = loan.originatedAmount();
        LocalDate startDate = loan.startDate();
        LocalDate dueDate = loan.startDate().plusMonths(1);
        LocalDate endDate = dueDate.minusDays(1);
        BigDecimal dailyRate = loan.targetInterestRate().divide(new BigDecimal(365),
                10,
                RoundingMode.HALF_UP);
        for (int i = 0; i < loan.term(); i++) {
            // Note: between is [startDate inclusive, dueDate exclusive)
            // For example: 1/1 -> 1/31, 2/1 -> 2/28
            // And so the correct numnber of days to calculate nterest
            // accrues from 1/1 -> 1/31 (31 days).
            long numDays = ChronoUnit.DAYS.between(startDate, dueDate);
            BigDecimal interestAmount = dailyRate.multiply(BigDecimal.valueOf(numDays))
                    .multiply(outstandingPrincipal)
                    .setScale(2, RoundingMode.HALF_UP);
            // Last installment
            BigDecimal principalAmount;
            if (i == loan.term() - 1) {
                principalAmount = outstandingPrincipal;
            } else {
                principalAmount = installmentAmount.subtract(interestAmount);
            }
            Installment installment = new Installment(
                    String.valueOf(i),
                    "",
                    i + 1,
                    principalAmount,
                    interestAmount,
                    startDate,
                    dueDate,
                    endDate,
                    InstallmentStatus.OWED);
            installments.add(installment);
            startDate = dueDate;
            dueDate = dueDate.plusMonths(1);
            endDate = dueDate.minusDays(1);
            outstandingPrincipal = outstandingPrincipal.subtract(principalAmount);
        }
        return installments;
    }

    public List<Installment> newInstallments(Loan loan) {
        // compute startDates
        // just compute days
        return getActual365AmortizationSchedule(loan);
    }
}
