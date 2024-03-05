package com.corefin.server.v1.request;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;

public record CreateLoanRequest(
                      int term,
                      BigDecimal originatedAmount,
                      String currency,
                      BigDecimal targetInterestRate,
                      BigDecimal effectiveInterestRate,
                      String externalReference,
                      LocalDate startDate,
                      LocalDate endDate,
                      String timezone,
                      String region,
                      String state
) {}
