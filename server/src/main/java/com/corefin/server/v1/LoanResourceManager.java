package com.corefin.server.v1;

import com.corefin.server.v1.request.CreateLoanRequest;
import com.corefin.server.v1.response.GetLoanResponse;
import org.corefin.dao.LoanDao;
import org.corefin.dto.LoanDto;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.UUID;

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

    public void createLoan(CreateLoanRequest createLoanRequest) {
        LoanDto loanDto = new LoanDto(
                null,
                createLoanRequest.term(),
                createLoanRequest.originatedAmount(),
                createLoanRequest.currency(),
                createLoanRequest.targetInterestRate(),
                createLoanRequest.effectiveInterestRate(),
                createLoanRequest.externalReference(),
                createLoanRequest.startDate(),
                createLoanRequest.endDate(),
                createLoanRequest.status(),
                createLoanRequest.timezone(),
                createLoanRequest.region(),
                createLoanRequest.state()
        );
        loanDao.insert(loanDto);
    }
}
