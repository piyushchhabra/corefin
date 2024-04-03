package org.corefin.calculator.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record Loan(
        String id,
        int term,
        BigDecimal originatedPrincipal,
        String currency,
        BigDecimal targetInterestRate,
        BigDecimal effectiveInterestRate,
        LocalDate startDate,
        String status,
        String timezone,

        // Fields below contain fields that would be computed
        List<Payment> payments,

        List<Installment> installments,

        @Deprecated
        // TODO: This field is broken atm. There's no use for it so i'm not fixing it now.
        BigDecimal accruedInterest,
        BigDecimal outstandingPrincipal
) {
    public Loan withUpdates(List<Installment> installments,
                            List<Payment> payments,
                            BigDecimal accruedInterest,
                            BigDecimal outstandingPrincipal) {
        return new Loan(
                id(),
                term(),
                originatedPrincipal(),
                currency(),
                targetInterestRate(),
                effectiveInterestRate(),
                startDate(),
                status(),
                timezone(),
                payments,
                installments,
                accruedInterest,
                outstandingPrincipal()
        );
    }
}
