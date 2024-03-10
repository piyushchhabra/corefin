package org.corefin.calculator.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public record Loan(
        String id,
        int term,
        BigDecimal originatedPrincipal,
        String currency,
        BigDecimal targetInterestRate,
        BigDecimal effectiveInterestRate,
        LocalDate startDate,
        LocalDate endDate,
        String status,
        String timezone
) {}
