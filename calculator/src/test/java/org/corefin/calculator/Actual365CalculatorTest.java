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
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;
import java.util.stream.Stream;

import static org.corefin.model.common.InstallmentStatus.EARLY;
import static org.corefin.model.common.InstallmentStatus.LATE;
import static org.corefin.model.common.InstallmentStatus.PAID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.assertj.core.api.Assertions.assertThat;


public class Actual365CalculatorTest {
    private Actual365CalculatorImpl actual365Calculator;
    private Loan loanConfig;
    private final BigDecimal originatedPrincipalAmount = new BigDecimal("1000.00");

    /**
     * Sets up the test fixture.
     * (Called before every test case method.)
     */
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

    /**
     * Tests the installment schedule generation functionality of the Actual/365 Calculator.
     * Given a 0% target interest rate, the calculator should return equal installments
     * without interest.
     */
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
        BigDecimal countingSum = BigDecimal.ZERO;
        for (Installment i : installments) {
            countingSum = countingSum.add(i.principalAmount());
        }

        assert countingSum.equals(originatedPrincipalAmount);
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
                                                new BigDecimal("165.14"),
                                                new BigDecimal("6.42"),
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
                                                new BigDecimal("170.11"),
                                                new BigDecimal("1.40"),
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
                LocalDate.of(2023, 01, 01),
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
                LocalDate.of(2023, 01, 01);
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
//        assert updatedLoanOneDayAfter.accruedInterest().compareTo(new BigDecimal("0.27")) == 0;
    }

    /**
     * Tests the installment updating functionality of the Actual/365 Calculator
     * for 1 on-time payment.
     */
    @Test
    public void testUpdateInstallmentsBasicAccrual_1OnTimePayment() {
        LocalDate startDate =
                LocalDate.of(2023, 01, 01);
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
        Loan updatedLoanOneMonthAfter = actual365Calculator.updateInstallments(loanConfig, startDate.plusMonths(1));

        // After the payment, the accruedInterest should be 0
//        assertThat(updatedLoanOneDayAfter.accruedInterest().compareTo(BigDecimal.ZERO)).isZero();
        assertThat(updatedLoanOneMonthAfter.installments().stream().filter(i -> i.status().equals(PAID)).count()).isEqualTo(1);
    }

    /**
     * Tests the installment updating functionality of the Actual/365 Calculator
     * for full on-time payments.
     */
    @Test
    public void testUpdateInstallmentsBasicAccrual_FullOnTimePayments() {
        LocalDate startDate =
                LocalDate.of(2023, 1, 1);
        List<Payment> payments = new ArrayList<>();
        // Create 6 full on-time payments
        for (int i = 0; i < 5; i++) {
            payments.add(
                    new Payment(
                            UUID.randomUUID().toString(),
                            new BigDecimal("171.56"),
                            startDate.plusMonths(i+1).atStartOfDay(ZoneId.of("UTC")),
                            PaymentType.PAYMENT,
                            new ArrayList<>())
            );
        }
        payments.add(
                new Payment(
                        UUID.randomUUID().toString(),
                        new BigDecimal("171.26"),
                        startDate.plusMonths(6).atStartOfDay(ZoneId.of("UTC")),
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
        Loan updatedLoanAfterAllInstallments =
                actual365Calculator.updateInstallments(loanConfig, startDate.plusMonths(6));

        assert updatedLoanAfterAllInstallments.installments().stream().filter(
                i -> i.status().equals(PAID)).count() == 6;

        List<Installment> expectedInstallments =
                actual365Calculator.getActual365AmortizationSchedule(loanConfig);

        // The amortization schedule should not be the same after full on-time payments are applied
        assertEquals(expectedInstallments.size(), updatedLoanAfterAllInstallments.installments().size());
        for (int i = 0; i < expectedInstallments.size(); i++) {
            Installment expectedInstallment = expectedInstallments.get(i);
            Installment updatedInstallment = updatedLoanAfterAllInstallments.installments().get(i);
            assertEquals(expectedInstallment.interestAmount(), updatedInstallment.interestAmount());
            assertEquals(expectedInstallment.principalAmount(), updatedInstallment.principalAmount());
        }
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
        // assertThat(updatedLoanOneDayAfter.accruedInterest().compareTo(BigDecimal.ZERO)).isGreaterThan(0);
        assertThat(updatedLoanOneDayAfter.installments().stream()
                .filter(i -> i.status().equals(LATE)).count()).isEqualTo(1);
    }

    @Test
    public void testUpdateInstallmentsBasicAccrual_1EarlyPayment() {
        LocalDate startDate =
                LocalDate.of(2023, 1, 1);
        ZonedDateTime paymentDate = startDate.plusDays(10)
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
        Loan updatedLoanOneDayAfter = actual365Calculator.updateInstallments(loanConfig, paymentDate.toLocalDate());
        // Assert updatedLoanOneDayAfter.accruedInterest().compareTo(BigDecimal.ZERO) == 0
        // accrued 10 days of interest, check that it got paid.
        assertThat(updatedLoanOneDayAfter.installments().get(0).interestAmount()).isEqualTo(new BigDecimal("2.74"));
        // the second installment will be accruing interest for about 50 days, and will be ~5x higher
        assertThat(updatedLoanOneDayAfter.installments().get(1).interestAmount()).isEqualTo(new BigDecimal("11.16"));
        assertThat(updatedLoanOneDayAfter.installments().stream().filter(
                i -> i.status().equals(EARLY)).count()).isEqualTo(1);
    }
    @Test
    public void testUpdateInstallments_2EarlyFullInstallmentPayments() {
        LocalDate startDate =
                LocalDate.of(2024, 1, 1);
        ZonedDateTime paymentDate = startDate.atStartOfDay(ZoneId.of("UTC"));
        List<Payment> payments = new ArrayList<>();
        payments.add(
                new Payment(
                        UUID.randomUUID().toString(),
                        new BigDecimal("171.56"),
                        paymentDate,
                        PaymentType.PAYMENT,
                        new ArrayList<>())
        );
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
        Loan updatedLoanOneDayAfter = actual365Calculator.updateInstallments(loanConfig, paymentDate.toLocalDate());
        assertThat(updatedLoanOneDayAfter.installments().get(2).interestAmount()).isEqualTo(new BigDecimal("16.38"));
        assertThat(updatedLoanOneDayAfter.installments().stream().filter(
                i -> i.status().equals(EARLY)).count()).isEqualTo(2);
    }

    /**
     * Tests the installment updating functionality of the Actual/365 Calculator
     * after one late full payment for the first installment.
     */
    @Test
    public void testUpdateInstallments_1LateFullInstallmentPayment() {
        LocalDate startDate =
                LocalDate.of(2023, 1, 1);

        // Create a payment for the first installment that's late by 27 days on 2/28 and apply it
        ZonedDateTime paymentDate =
                startDate.atStartOfDay(ZoneId.of("UTC")).plusMonths(1).plusDays(28);
        List<Payment> payments = new ArrayList<>();
        payments.add(
                new Payment(
                        UUID.randomUUID().toString(),
                        new BigDecimal("171.56"),
                        paymentDate,
                        PaymentType.PAYMENT,
                        new ArrayList<>())
        );

        // Create a payment for the second installment that's on-time

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

        Loan updatedLoan = actual365Calculator.updateInstallments(loanConfig, paymentDate.toLocalDate());
        List<Installment> schedule =  updatedLoan.installments();
        assertEquals(schedule.get(0).interestAmount(), BigDecimal.valueOf(16.16));
        assertEquals(schedule.get(0).principalAmount(),
                BigDecimal.valueOf(155.4).setScale(2, RoundingMode.DOWN));
        assertThat(schedule.get(0).status()).isEqualTo(LATE);
    }
}
