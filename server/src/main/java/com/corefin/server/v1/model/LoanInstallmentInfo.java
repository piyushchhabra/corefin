package com.corefin.server.v1.model;

import org.corefin.model.common.InstallmentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

public record LoanInstallmentInfo(
        String installmentId,
        String loanId,
        int numTerm, // which installment term this is
        BigDecimal principalAmount,
        BigDecimal interestAmount,
        LocalDate startDate,
        LocalDate dueDate,
        LocalDate endDate,
        InstallmentStatus status)
{
}
