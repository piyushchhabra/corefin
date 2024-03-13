package org.corefin.calculator.model;


import org.corefin.model.common.InstallmentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;


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
    public Installment withUpdates(BigDecimal principalAmount,
                                   BigDecimal interestAmount,
                                   InstallmentStatus status) {
        return new Installment(
                installmentId(),
                loanId(),
                numTerm(),
                principalAmount,
                interestAmount,
                startDate(),
                dueDate(),
                endDate(),
                status
        );
    }

    public boolean isLocked(InstallmentStatus status) {
        return status.equals(InstallmentStatus.CANCELED) ||
                status.equals(InstallmentStatus.REFUNDED) ||
                status.equals(InstallmentStatus.LATE) ||
                status.equals(InstallmentStatus.EARLY) ||
                status.equals(InstallmentStatus.PAID);
    }
}
