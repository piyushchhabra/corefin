package org.corefin.calculator.model;

import java.math.BigDecimal;

public record InstallmentMapping(
        String loanInstallmentId,
        String paymentId,
        BigDecimal principalAmount,
        BigDecimal interestAmount) { }
