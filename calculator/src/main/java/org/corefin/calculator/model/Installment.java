package org.corefin.calculator.model;


import org.corefin.model.common.InstallmentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

public record Installment(
        String installmentId,
        String loanId,
        int numTerm,
        BigDecimal principalAmount,
        BigDecimal interestAmount,
        LocalDate startDate,
        LocalDate dueDate,
        LocalDate endDate,
        InstallmentStatus status
) {
}
