package org.corefin.calculator.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;

public record Installment(
        String installmentId,
        String loanId,
        int numTerm,
        BigDecimal principalAmount,
        BigDecimal interestAmount,
        LocalDate startDate,
        LocalDate dueDate,
        InstallmentStatus status
) {
}
