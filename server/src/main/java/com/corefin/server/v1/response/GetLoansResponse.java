package com.corefin.server.v1.response;

import com.corefin.server.v1.model.LoanInfo;

import javax.validation.constraints.NotNull;
import java.util.List;

public record GetLoansResponse(
        @NotNull
        List<LoanInfo> loans
) {
}
