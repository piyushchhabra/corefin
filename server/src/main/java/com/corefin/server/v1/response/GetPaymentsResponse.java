package com.corefin.server.v1.response;

import org.corefin.dto.PaymentDto;

import java.util.List;

public record GetPaymentsResponse(
    List<PaymentDto> payments
) {}
