package com.corefin.server.v1;

import com.corefin.server.v1.response.GetLoanResponse;
import org.corefin.dao.LoanDao;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service
public class LoanResourceManager {

    private LoanDao loanDao;

    @Inject
    public LoanResourceManager(LoanDao loanDao) {
        this.loanDao = loanDao;
    }

    public GetLoanResponse doGetLoan(String loanId) {
        return new GetLoanResponse(loanId);
    }
}
