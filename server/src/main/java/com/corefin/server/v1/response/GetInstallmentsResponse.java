package com.corefin.server.v1.response;

import org.corefin.dto.LoanInstallmentDto;

import java.util.List;

public record GetInstallmentsResponse(
        List<LoanInstallmentDto> installments
) {}
