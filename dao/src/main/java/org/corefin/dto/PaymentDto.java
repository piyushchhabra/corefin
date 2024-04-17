package org.corefin.dto;


import org.corefin.model.common.PaymentType;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

public record PaymentDto(
        String paymentId,
        String loanId,
        BigDecimal amount,
        PaymentType paymentType,
        ZonedDateTime paymentDateTime
) { }
