package org.corefin.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;

// TODO: Fill this in
public record LoanDto(String loanId,
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
                      String state
                      ) {
}
