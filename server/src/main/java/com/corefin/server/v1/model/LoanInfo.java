package com.corefin.server.v1.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record LoanInfo (
    String loanId,
    int term,
    BigDecimal originatedAmount,
    String currency,
    BigDecimal targetInterestRate,
    BigDecimal effectiveInterestRate,
    String externalReference,
    LocalDate startDate,
    LocalDate endDate,
    String status,
    String timezone,
    String region,
    String state,
    List<LoanInstallmentInfo> loanInstallments) {
}
