package org.corefin.calculator;

import org.corefin.calculator.model.Installment;
import org.corefin.calculator.model.Loan;
import org.corefin.calculator.model.Payment;
import org.corefin.model.common.InstallmentStatus;
import org.corefin.model.common.PaymentType;
import org.joda.money.CurrencyUnit;
import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;
import java.util.stream.Stream;

import static org.corefin.model.common.InstallmentStatus.LATE;
import static org.corefin.model.common.InstallmentStatus.PAID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Actual365CalculatorTest {
    private Actual365CalculatorImpl actual365Calculator;
    private Loan loanConfig;

    private BigDecimal originatedPrincipalAmount = new BigDecimal("1000.00");

    @BeforeEach
    public void init() {
        actual365Calculator = new Actual365CalculatorImpl();
        loanConfig = new Loan(
                "loanId",
                12,
                originatedPrincipalAmount,
                CurrencyUnit.USD.toString(),
                new BigDecimal("0.08"),
                new BigDecimal("0.08"),
                LocalDate.now(),
                "IN_PROGRESS",
                TimeZone.getTimeZone("America/Los_Angeles").toString(),
                new ArrayList<>(),
                new ArrayList<>(),
                originatedPrincipalAmount,
                BigDecimal.ZERO
        );
    }

    @Test
    public void testZeroInterestEqualMonthlyInstallment() {
        loanConfig = new Loan(
                "loanId",
                12,
                BigDecimal.valueOf(1000L),
                CurrencyUnit.USD.toString(),
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                LocalDate.now(),
                "IN_PROGRESS",
                TimeZone.getTimeZone("America/Los_Angeles").toString(),
                new ArrayList<>(),
                new ArrayList<>(),
                originatedPrincipalAmount,
                BigDecimal.ZERO
        );
        List<Installment> installments = actual365Calculator.newInstallments(loanConfig);
        assertPrincipalAmount(installments, originatedPrincipalAmount);
    }
//    BigDecimal originatedPrincipalAmount,
//    BigDecimal targetAPR,
//    int term,
//    BigDecimal expectedEmi
    private static Stream<Arguments> emiTestGenerator() {
        return Stream.of(
                Arguments.of(
                        new BigDecimal(1300),
                        new BigDecimal("0.00"),
                        12, // in months
                        new BigDecimal("108.33")
                ),
                Arguments.of(
                        new BigDecimal(1000),
                        new BigDecimal("0.10"),
                        6, // in months
                        new BigDecimal("171.56")
                ),
                Arguments.of(
                        new BigDecimal(10000),
                        new BigDecimal("0.10"),
                        60,
                        new BigDecimal("212.47")
                )
        );
    }
    @ParameterizedTest
    @MethodSource("emiTestGenerator")
    public void testEmiCalculation(BigDecimal originatedPrincipalAmount,
                                   BigDecimal targetAPR,
                                   int term,
                                   BigDecimal expectedEmi) {
        loanConfig = new Loan(
                "loanId",
                term,
                originatedPrincipalAmount,
                CurrencyUnit.USD.toString(),
                targetAPR,
                targetAPR,
                LocalDate.now(),
                "IN_PROGRESS",
                TimeZone.getTimeZone("America/Los_Angeles").toString(),
                new ArrayList<>(),
                new ArrayList<>(),
                originatedPrincipalAmount,
                BigDecimal.ZERO
        );
        assertEquals(expectedEmi, actual365Calculator.getEquatedMonthlyInstallment(loanConfig));
    }


    private static Stream<Arguments> newInstallmentTestGenerator() {
        return Stream.of(
                // Originated principal
                // TargetAPR
                // Term (in months)
                // List of expected Installments
                Arguments.of(
                        new BigDecimal(1000),
                        new BigDecimal("0.10"),
                        6, // in months
                        new ArrayList<Installment>(
                                Arrays.asList(
                                        new Installment(
                                                "", "", 1,
                                                new BigDecimal("163.07"),
                                                new BigDecimal("8.49"),
                                                LocalDate.now(), LocalDate.now(), LocalDate.now(), // unused
                                                InstallmentStatus.OWED
                                        ),
                                        new Installment(
                                                "", "", 2,
                                                new BigDecimal("164.68"),
                                                new BigDecimal("6.88"),
                                                LocalDate.now(), LocalDate.now(), LocalDate.now(), // unused
                                                InstallmentStatus.OWED
                                        ),
                                        new Installment(
                                                "", "", 3,
                                                new BigDecimal("165.85"),
                                                new BigDecimal("5.71"),
                                                LocalDate.now(), LocalDate.now(), LocalDate.now(), // unused
                                                InstallmentStatus.OWED
                                        ),
                                        new Installment(
                                                "", "", 4,
                                                new BigDecimal("167.40"),
                                                new BigDecimal("4.16"),
                                                LocalDate.now(), LocalDate.now(), LocalDate.now(), // unused
                                                InstallmentStatus.OWED
                                        ),
                                        new Installment(
                                                "", "", 5,
                                                new BigDecimal("168.68"),
                                                new BigDecimal("2.88"),
                                                LocalDate.now(), LocalDate.now(), LocalDate.now(), // unused
                                                InstallmentStatus.OWED
                                        ),
                                        new Installment(
                                                "", "", 6,
                                                new BigDecimal("170.32"),
                                                new BigDecimal("1.45"),
                                                LocalDate.now(), LocalDate.now(), LocalDate.now(), // unused
                                                InstallmentStatus.OWED
                                        )
                                )
                        )
                )
        );
    }
    @ParameterizedTest
    @MethodSource("newInstallmentTestGenerator")
    public void testNewInstallmentGenerator(BigDecimal originatedPrincipalAmount,
                                            BigDecimal targetAPR,
                                            int term,
                                            List<Installment> expectedInstallments) {
        loanConfig = new Loan(
                "loanId",
                term,
                originatedPrincipalAmount,
                CurrencyUnit.USD.toString(),
                targetAPR,
                targetAPR,
                LocalDate.of(2024, 03, 06),
                "IN_PROGRESS",
                TimeZone.getTimeZone("America/Los_Angeles").toString(),
                new ArrayList<>(),
                new ArrayList<>(),
                originatedPrincipalAmount,
                BigDecimal.ZERO
        );

        List<Installment> resultInstallments = actual365Calculator.newInstallments(loanConfig);
        assertEquals(expectedInstallments.size(), resultInstallments.size());
        for (int i = 0; i < expectedInstallments.size() - 1; i++) {
            Installment expectedInstallment = expectedInstallments.get(i);
            Installment resultInstallment = resultInstallments.get(i);

            assertEquals(expectedInstallment.principalAmount(),
                    resultInstallment.principalAmount());
            assertEquals(expectedInstallment.interestAmount(),
                    resultInstallment.interestAmount());
            assertEquals(expectedInstallment.numTerm(),
                    resultInstallment.numTerm());

        }
    }

    @Test
    public void testUpdateInstallmentsAccrues_1Day_FilterByCalculationDate() {
        List<Payment> payments = new ArrayList<>();
        LocalDate startDate =
                LocalDate.of(2024, 01, 01);
        payments.add(
                new Payment(
                        UUID.randomUUID().toString(),
                        new BigDecimal("171.56"),
                        startDate.plusMonths(1).atStartOfDay(ZoneId.of("UTC")),
                        PaymentType.PAYMENT,
                        new ArrayList<>())
        );
        loanConfig = new Loan(
                "loanId",
                6,
                originatedPrincipalAmount,
                CurrencyUnit.USD.toString(),
                new BigDecimal("0.10"),
                new BigDecimal("0.10"),
                startDate,
                "IN_PROGRESS",
                TimeZone.getTimeZone("America/Los_Angeles").toString(),
                payments,
                new ArrayList<>(),
                originatedPrincipalAmount,
                BigDecimal.ZERO
        );
        Loan updatedLoanOneDayAfter = actual365Calculator.updateInstallments(loanConfig, startDate.plusDays(1));
        // Accrues 1 day of interest, payment shouldn't apply
        assert updatedLoanOneDayAfter.accruedInterest().compareTo(new BigDecimal("0.27")) == 0;
    }

    /*
     * Tests
     * - Pay down accrued interest for 1 on-time payment. Check Loan.accruedInterest == 0
     */
    @Test
    public void testUpdateInstallmentsBasicAccrual_1OnTimePayment() {
        LocalDate startDate =
                LocalDate.of(2024, 01, 01);
        List<Payment> payments = new ArrayList<>();
        payments.add(
                new Payment(
                        UUID.randomUUID().toString(),
                        new BigDecimal("171.56"),
                        startDate.plusMonths(1).atStartOfDay(ZoneId.of("UTC")),
                        PaymentType.PAYMENT,
                        new ArrayList<>())
        );
        loanConfig = new Loan(
                "loanId",
                6,
                originatedPrincipalAmount,
                CurrencyUnit.USD.toString(),
                new BigDecimal("0.10"),
                new BigDecimal("0.10"),
                startDate,
                "IN_PROGRESS",
                TimeZone.getTimeZone("America/Los_Angeles").toString(),
                payments,
                new ArrayList<>(),
                originatedPrincipalAmount,
                BigDecimal.ZERO
        );
        Loan updatedLoanOneDayAfter = actual365Calculator.updateInstallments(loanConfig, startDate.plusMonths(1));

        // After the payment, the accruedInterest should be 0
        assert updatedLoanOneDayAfter.accruedInterest().compareTo(BigDecimal.ZERO) == 0;
        assert updatedLoanOneDayAfter.installments().stream().filter(
                i -> i.status().equals(PAID)).count() == 1;
    }

    @Test
    public void testUpdateInstallmentsBasicAccrual_1LatePayment() {
        LocalDate startDate =
                LocalDate.of(2024, 01, 01);
        ZonedDateTime paymentDate = startDate.plusMonths(1).plusDays(1)
                .atStartOfDay(ZoneId.of("UTC"));
        List<Payment> payments = new ArrayList<>();
        payments.add(
                new Payment(
                        UUID.randomUUID().toString(),
                        new BigDecimal("171.56"),
                        paymentDate,
                        PaymentType.PAYMENT,
                        new ArrayList<>())
        );
        loanConfig = new Loan(
                "loanId",
                6,
                originatedPrincipalAmount,
                CurrencyUnit.USD.toString(),
                new BigDecimal("0.10"),
                new BigDecimal("0.10"),
                startDate,
                "IN_PROGRESS",
                TimeZone.getTimeZone("America/Los_Angeles").toString(),
                payments,
                new ArrayList<>(),
                originatedPrincipalAmount,
                BigDecimal.ZERO
        );
        Loan updatedLoanOneDayAfter = actual365Calculator.updateInstallments(loanConfig, paymentDate.toLocalDate().plusDays(1));
        assert updatedLoanOneDayAfter.accruedInterest().compareTo(BigDecimal.ZERO) > 0;
        assert updatedLoanOneDayAfter.installments().stream().filter(
                i -> i.status().equals(LATE)).count() == 1;
    }

    private void assertPrincipalAmount(List<Installment> installments, BigDecimal amount) {
        BigDecimal countingSum = BigDecimal.ZERO;
        for (Installment i : installments) {
            countingSum = countingSum.add(i.principalAmount());
        }
        assert countingSum.equals(amount);
    }
}
