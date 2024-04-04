package com.corefin.server.v1.response;

import org.corefin.dto.PaymentDto;

public record GetPaymentResponse(
        PaymentDto payment
) {}

