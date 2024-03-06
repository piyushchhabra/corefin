package org.corefin.calculator;

import org.corefin.calculator.model.Installment;
import org.corefin.calculator.model.Loan;
import org.joda.money.CurrencyUnit;
import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
                LocalDate.now(),
                "IN_PROGRESS",
                TimeZone.getTimeZone("America/Los_Angeles").toString(),
                new ArrayList<>(),
                new ArrayList<>()
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
                LocalDate.now(),
                "IN_PROGRESS",
                TimeZone.getTimeZone("America/Los_Angeles").toString(),
                new ArrayList<>(),
                new ArrayList<>()
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
                LocalDate.now(),
                "IN_PROGRESS",
                TimeZone.getTimeZone("America/Los_Angeles").toString(),
                new ArrayList<>(),
                new ArrayList<>()
        );
        assertEquals(expectedEmi, actual365Calculator.getEquatedMonthlyInstallment(loanConfig));
    }

    private void assertPrincipalAmount(List<Installment> installments, BigDecimal amount) {
        BigDecimal countingSum = BigDecimal.ZERO;
        for (Installment i : installments) {
            countingSum = countingSum.add(i.principalAmount());
        }
        assert countingSum.equals(amount);
    }
}
