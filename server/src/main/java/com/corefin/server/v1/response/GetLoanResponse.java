package com.corefin.server.v1.response;

import com.corefin.server.v1.model.LoanInfo;

import javax.validation.constraints.NotNull;

public record GetLoanResponse(
        @NotNull
        LoanInfo loanInfo
) {}
