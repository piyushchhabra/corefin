package com.corefin.server.v1;

import com.corefin.server.v1.response.GetLoanResponse;
import org.springframework.stereotype.Service;

@Service
public class LoanResourceManager {

    public LoanResourceManager() { }

    public GetLoanResponse doGetLoan(String loanId) {
        return new GetLoanResponse(loanId);
    }
}