package org.corefin.dto;

import org.corefin.calculator.model.InstallmentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;

public record LoanInstallmentDto (
        String installmentId,
        String loanId,
        int numTerm, // which installment term this is
        BigDecimal principalAmount,
        BigDecimal interestAmount,
        LocalDate startDate,
        LocalDate dueDate,
        LocalDate endDate,
        InstallmentStatus status
) { }
