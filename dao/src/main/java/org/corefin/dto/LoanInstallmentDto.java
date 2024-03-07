package org.corefin.dto;


import org.corefin.model.common.InstallmentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

public record LoanInstallmentDto (
        String installmentId,
        InstallmentStatus status,
        String loanId,
        int numTerm, // which installment term this is
        BigDecimal principalAmount,
        BigDecimal interestAmount,
        LocalDate startDate,
        LocalDate dueDate,
        LocalDate endDate
) { }
