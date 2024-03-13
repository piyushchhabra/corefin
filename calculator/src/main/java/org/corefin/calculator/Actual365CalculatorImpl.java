package org.corefin.calculator;

import lombok.Getter;
import lombok.Setter;
import org.corefin.calculator.model.Installment;
import org.corefin.calculator.model.Loan;
import org.corefin.calculator.model.Payment;
import org.corefin.calculator.model.PaymentStatus;
import org.corefin.model.common.InstallmentStatus;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.logging.Logger;

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
    private static final Logger LOGGER = Logger.getLogger(Actual365CalculatorImpl.class.getName());

    // rate is annual compounding interest rate / number of payments per period
    public BigDecimal getEquatedMonthlyInstallment(Loan loan) {
        BigDecimal monthlyRate = loan.targetInterestRate().divide(new BigDecimal(12),
                10,
                RoundingMode.HALF_UP);
        // If it's a 0% interest rate, then just do originated loan amount / number of terms
        if (loan.targetInterestRate().compareTo(BigDecimal.ZERO) == 0) {
            return loan.originatedPrincipal().divide(new BigDecimal(loan.term()), 2, RoundingMode.HALF_UP);
        }
        return monthlyRate.multiply(monthlyRate.add(BigDecimal.ONE).pow(loan.term())).divide(
                (monthlyRate.add(BigDecimal.ONE).pow(loan.term())).subtract(BigDecimal.ONE),
                10,
                RoundingMode.HALF_UP
        ).multiply(loan.originatedPrincipal()).setScale(2, RoundingMode.HALF_UP);
    }

    public List<Installment> getActual365AmortizationSchedule(Loan loan) {
        List<Installment> installments = new ArrayList<>();
        BigDecimal installmentAmount = getEquatedMonthlyInstallment(loan);
        BigDecimal outstandingPrincipal = loan.originatedPrincipal();
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


    public Loan updateInstallments(Loan loan, LocalDate calculationDate) {
        // TODO(hubert): input existing installments and verify that List<Installment> have the correct p/i breakdown
        // TODO: Implement calculation date
        // - if calculation Date is before paymentDate, then break (throw)
        // - calculation date is used to compute how much interest we've accrued (running balance).
        // - then we can generate installments
        // TODO: Generate installment mappings from payment inputs
        // TODO: Generate payment UUID, compute mappings and then use same UUID to persist to DB
        RunningBalance runningBalance = new RunningBalance(
                loan.originatedPrincipal(),
                BigDecimal.ZERO,
                loan.targetInterestRate(),
                loan.startDate());
        List<Installment> installments = newInstallments(loan);
        PriorityQueue<Installment> installmentQueue = new PriorityQueue<>(Comparator.comparing(Installment::dueDate));
        PriorityQueue<Payment> paymentQueue = new PriorityQueue<>(Comparator.comparing(Payment::paymentDateTime));
        installments.forEach(installment -> installmentQueue.add(installment));
        loan.payments().stream()
                .filter(payment -> payment.paymentDateTime().toLocalDate().isBefore(calculationDate) ||
                        payment.paymentDateTime().toLocalDate().isEqual(calculationDate))
                .forEach(payment -> paymentQueue.add(payment));

        List<Payment> payments = new ArrayList<>();

        while (!installmentQueue.isEmpty()) {
            Installment installment = installmentQueue.poll();

            processPaymentEventsByCutoffDate(runningBalance, calculationDate, paymentQueue, installments, payments);
            rollDateForwardAndAccrueInterest(runningBalance, calculationDate, installment, installments);
            // RollDateForwardAndAccrueInterest (takes in RB to calculate future installment amounts)
        }

        return loan.withUpdates(
                installments,
                payments,
                runningBalance.accruedInterest,
                runningBalance.outstandingPrincipal
        );
    }

    // Moves calculation Date forward, and accrues interest to RunningBalance if applicable.
    // Then, updates `installments` list.
    private void rollDateForwardAndAccrueInterest(RunningBalance runningBalance,
                                                  LocalDate inputCalculationDate,
                                                  Installment installment,
                                                  List<Installment> installments) {
        // Need to update accrued interest, and estimate interest separatelyp
        // accrue up to inputCalculation Date (if it's within the bounds)
        // for Installment, accrue interest from the last calculationDate to installmentEndDate
        // - Update the runningBalance Date to end of installment date.
        // - subtract interest/principal amounts on estimate

        // Get the latest installments update if there was a payment involved
        installment = installments.get(installment.numTerm() - 1);

        LocalDate dateToAccrueTo = installment.dueDate();

        boolean shouldAccrue = false;
//        if (inputCalculationDate.isBefore(installment.startDate())) {
//            // do something
//        }
        boolean estimateInstallmentInterestSeparately = false;
        if (inputCalculationDate.isAfter(installment.dueDate()) ||
                inputCalculationDate.isEqual(installment.dueDate())) {
            dateToAccrueTo = installment.dueDate();
            shouldAccrue = true;
        }
        // Calculation Date is in between installment start/end. So
        // loan accrues interest but we still need to do the full estimation
        else if (inputCalculationDate.isAfter(installment.startDate()) && inputCalculationDate.isBefore(installment.endDate())) {
            dateToAccrueTo = inputCalculationDate;
            shouldAccrue = true;
            estimateInstallmentInterestSeparately = true;
        }
        if (runningBalance.currentCalculationDate.isAfter(dateToAccrueTo)) {
            return;
        }
        // Installment estimated interest is based on outstandingPrincipal. so it's not effected by accrued interest
        // AccruedInterest is directly affected by the inputCalculation date
        BigDecimal dailyRate = runningBalance.getTargetAPR().divide(new BigDecimal(365),
                10,
                RoundingMode.HALF_UP);
        long numDays = ChronoUnit.DAYS.between(runningBalance.currentCalculationDate, dateToAccrueTo);
        BigDecimal installmentAmount = installment.interestAmount().add(installment.principalAmount());
        BigDecimal accruedInterest = dailyRate.multiply(BigDecimal.valueOf(numDays))
                .multiply(runningBalance.outstandingPrincipal)
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal principalAmount = installmentAmount.subtract(accruedInterest);
        if (shouldAccrue) {
            runningBalance.setAccruedInterest(runningBalance.getAccruedInterest().add(accruedInterest));
        }
        // For the scenario, where the calculationDate is within the installment bounds,
        // We need to accrue to RunningBalance AND estimate the InstallmentAmounts separately.
        if (estimateInstallmentInterestSeparately) {
            numDays = ChronoUnit.DAYS.between(runningBalance.currentCalculationDate, installment.dueDate());
            accruedInterest = dailyRate.multiply(BigDecimal.valueOf(numDays))
                    .multiply(runningBalance.outstandingPrincipal)
                    .setScale(2, RoundingMode.HALF_UP);
            accruedInterest = accruedInterest.add(runningBalance.accruedInterest);
            principalAmount = installmentAmount.subtract(accruedInterest);
        }

        if (!installment.isLocked(installment.status())) {
            Installment updatedInstallmentEstimate = new Installment(
                    installment.installmentId(),
                    installment.loanId(),
                    installment.numTerm(),
                    principalAmount,
                    accruedInterest,
                    installment.startDate(),
                    installment.dueDate(),
                    installment.endDate(),
                    installment.status());
            // Update the global installments list
            installments.set(installment.numTerm() - 1, updatedInstallmentEstimate);
        }

        runningBalance.setCurrentCalculationDate(installment.dueDate());
        runningBalance.setOutstandingPrincipal(runningBalance.getOutstandingPrincipal().subtract(principalAmount));






        // Prior
        // Early
        // Late
        // Ontime payment

//
//
//        // TODO: Math.min(InstallmentAmount, updatedInterestAmount)
//        updatedInterestAmount = runningBalance.getTargetAPR().multiply(BigDecimal.valueOf(numDays))
//                .multiply(runningBalance.outstandingPrincipal)
//                .setScale(2, RoundingMode.HALF_UP);
//        updatedPrincipalAmount = firstUnpaidInstallment.principalAmount().add(firstUnpaidInstallment.interestAmount())
//                .subtract(updatedInterestAmount);


    }

    // TODO: Fix -> cutoff is installment cutoff, not calculationDate cutoff
    private void processPaymentEventsByCutoffDate(RunningBalance runningBalance,
                                                  LocalDate cutoff,
                                                  Queue<Payment> paymentEvents,
                                                  List<Installment> installments,
                                                  List<Payment> payments) {
        while (!paymentEvents.isEmpty()) {
            Payment p = paymentEvents.peek();
            // Only process payment events up to cutoff date.
            // If there's a LATE payment, then this will ensure that we can update a previous
            // installment to paid while maintain our accrue logic.
            if (p.paymentDateTime().toLocalDate().isAfter(cutoff)) {
                break;
            }
            p = paymentEvents.poll();
            payments.add(p);
            // else
            long numDays = ChronoUnit.DAYS.between(runningBalance.currentCalculationDate, p.paymentDateTime());
            // TODO: Math.min(InstallmentAmount, updatedInterestAmount)
            BigDecimal dailyRate = runningBalance.getTargetAPR().divide(new BigDecimal(365),
                    10,
                    RoundingMode.HALF_UP);
            BigDecimal accruedInterestAmountSinceLastCalculation = dailyRate.multiply(BigDecimal.valueOf(numDays))
                    .multiply(runningBalance.outstandingPrincipal)
                    .setScale(2, RoundingMode.HALF_UP);
            runningBalance.setAccruedInterest(runningBalance.getAccruedInterest().add(accruedInterestAmountSinceLastCalculation));
            switch (p.paymentType()) {
                case PAYMENT ->
                {
                    // 1: Verify this is a full installment payment
                    // get the first unpaid installment
                    // TODO: make sure this list is sorted?
                    Optional<Installment> firstUnpaidInstallmentOptional =
                            installments.stream()
                                    .filter(installment -> installment.status().equals(InstallmentStatus.OWED))
                                    .findFirst();
                    if (firstUnpaidInstallmentOptional.isEmpty()) {
                        throw new RuntimeException("No unpaid installment found");
                    }
                    Installment firstUnpaidInstallment = firstUnpaidInstallmentOptional.get();
                    // TODO: This needs to be updated for custom payment amounts
                    BigDecimal unpaidInstallmentAmount = firstUnpaidInstallment.interestAmount()
                            .add(firstUnpaidInstallment.principalAmount());
                    if (p.amount().compareTo(unpaidInstallmentAmount) != 0) {
                        LOGGER.severe("Payment amount and installment amounts don't match. Payment amount: [%s], " +
                                "Installment amount: [%s]".formatted(p.amount(), unpaidInstallmentAmount));
                        throw new RuntimeException("Corefin doesn't support custom payment amounts");
                    }
                    // if it matches, then...
                    // First find if this is early, late, or ontime
                    // if it's late, it's directly the amortization schedule (since locked)
                    // if it's early, then recompute
                    // if it's on-time, then it's also directly amortization schedule
                    //
                    PaymentStatus paymentStatus = PaymentStatus.ON_TIME;
                    InstallmentStatus installmentStatus = InstallmentStatus.PAID;
                    if (p.paymentDateTime().toLocalDate().isBefore(firstUnpaidInstallment.dueDate())) {
                        paymentStatus = PaymentStatus.EARLY;
                        installmentStatus = InstallmentStatus.EARLY;
                    } else if (p.paymentDateTime().toLocalDate().isAfter(firstUnpaidInstallment.dueDate())) {
                        paymentStatus = PaymentStatus.LATE;
                        installmentStatus = InstallmentStatus.LATE;
                    }
                    int installmentIndex = firstUnpaidInstallment.numTerm() - 1;
                    BigDecimal updatedPrincipalAmount = firstUnpaidInstallment.principalAmount();
                    BigDecimal updatedInterestAmount = firstUnpaidInstallment.interestAmount();
                    // even if late, need to calculate, since there's extra days
//                    if (paymentStatus.equals(PaymentStatus.EARLY)) {
                        // recompute the principalAmount, interestamount
//                    long numDays = ChronoUnit.DAYS.between(runningBalance.currentCalculationDate, p.paymentDateTime());
//                    // TODO: Math.min(InstallmentAmount, updatedInterestAmount)
//                    BigDecimal dailyRate = runningBalance.getTargetAPR().divide(new BigDecimal(365),
//                            10,
//                            RoundingMode.HALF_UP);
                    updatedInterestAmount = dailyRate.multiply(BigDecimal.valueOf(numDays))
                            .multiply(runningBalance.outstandingPrincipal)
                            .setScale(2, RoundingMode.HALF_UP);
                    updatedPrincipalAmount = firstUnpaidInstallment.principalAmount().add(firstUnpaidInstallment.interestAmount())
                            .subtract(updatedInterestAmount);

//                    }
                    Installment updatedInstallment = firstUnpaidInstallment.withUpdates(
                            // P and I are the same as the amortization schedule for late/on-time
                            updatedPrincipalAmount,
                            updatedInterestAmount,
                            installmentStatus
                    );
                    installments.set(installmentIndex, updatedInstallment);

                    runningBalance.setOutstandingPrincipal(runningBalance.getOutstandingPrincipal().subtract(updatedPrincipalAmount));
                    runningBalance.setAccruedInterest(runningBalance.getAccruedInterest().subtract(updatedInterestAmount));
                    runningBalance.setCurrentCalculationDate(p.paymentDateTime().toLocalDate());
                }

            }

        }

    }

    private PaymentStatus getPaymentStatus(LocalDate paymentDate, LocalDate installmentDueDate) {
        if (paymentDate.isBefore(installmentDueDate)) {
            return PaymentStatus.EARLY;
        } else if (paymentDate.isAfter(installmentDueDate)) {
            return PaymentStatus.LATE;
        }
        return PaymentStatus.ON_TIME;
    }

    private record CalculationEvent(
            LocalDate eventDate,
            CalculationType calculationType,
            // for Installment: this is the EMI.
            // for payment, this is payment amount
            Optional<Payment> payment,
            Optional<Installment> installment
    ) { }

    private enum CalculationType {
        INSTALLMENT, // installment due dates
        PAYMENT // payment event type
    };

    // RunningBalance is the internal data structure we will use to
    // maintain the state of the calculations
    private class RunningBalance {
        @Getter
        @Setter
        BigDecimal outstandingPrincipal;
        @Getter
        @Setter
        BigDecimal accruedInterest;
        @Getter
        @Setter
        BigDecimal targetAPR;
        @Getter
        @Setter
        LocalDate currentCalculationDate;
        public RunningBalance(
                BigDecimal outstandingPrincipal,
                BigDecimal accruedInterest,
                BigDecimal targetAPR,
                LocalDate currentCalculationDate
        ) {
            this.outstandingPrincipal = outstandingPrincipal;
            this.accruedInterest = accruedInterest;
            this.targetAPR = targetAPR;
            this.currentCalculationDate = currentCalculationDate;
        }


//        public void setAccruedInterest(BigDecimal accruedInterest) {
//            this.accruedInterest = accruedInterest;
//        }
//
//        public void setCurrentCalculationDate(LocalDate currentCalculationDate) {
//            this.currentCalculationDate = currentCalculationDate;
//        }

    }
}
