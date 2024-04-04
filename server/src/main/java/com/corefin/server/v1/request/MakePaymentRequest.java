package com.corefin.server.v1.request;


import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

public record MakePaymentRequest(
        @Valid
        String loanId,
        @Valid
        BigDecimal amount,
        @Valid
        String paymentType,
        @Valid
        ZonedDateTime paymentDateTime
) {
}
