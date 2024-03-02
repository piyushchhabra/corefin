package org.corefin.calculator.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

public record Loan(
        String id,
        int term,
        BigDecimal originatedAmount,
        String currency,
        BigDecimal targetInterestRate,
        BigDecimal effectiveInterestRate,
        LocalDate startDate,
        LocalDate endDate,
        String status,
        String timezone,
        List<Installment> installments,
        List<Payment> payments
) {}
