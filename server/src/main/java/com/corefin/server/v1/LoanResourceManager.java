package com.corefin.server.v1;

import com.corefin.server.transform.LoanInstallmentTransformer;
import com.corefin.server.transform.LoanTransformer;
import com.corefin.server.v1.request.CreateLoanRequest;
import com.corefin.server.v1.response.GetLoanResponse;
import org.corefin.calculator.Actuarial365Calculator;
import org.corefin.calculator.model.Installment;
import org.corefin.dao.LoanDao;
import org.corefin.dao.LoanInstallmentDao;
import org.corefin.dto.LoanDto;
import org.corefin.dto.LoanInstallmentDto;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;

@Service
public class LoanResourceManager {

    private Actuarial365Calculator calculator;
    private LoanDao loanDao;
    private LoanInstallmentDao loanInstallmentDao;

    @Inject
    public LoanResourceManager(LoanDao loanDao,
                               LoanInstallmentDao loanInstallmentDao) {
        this.calculator = new Actuarial365Calculator();
        this.loanDao = loanDao;
        this.loanInstallmentDao = loanInstallmentDao;

    }

    public GetLoanResponse doGetLoan(String loanId) {
        return new GetLoanResponse(loanId);
    }

    public String createLoan(CreateLoanRequest createLoanRequest) {
        String loanId = UUID.randomUUID().toString();
        LoanDto loanDto = new LoanDto(
                loanId,
                createLoanRequest.term(),
                createLoanRequest.originatedAmount(),
                createLoanRequest.currency(),
                createLoanRequest.targetInterestRate(),
                createLoanRequest.effectiveInterestRate(),
                createLoanRequest.externalReference(),
                createLoanRequest.startDate(),
                createLoanRequest.endDate(),
                "ORIGINATED",
                createLoanRequest.timezone(),
                createLoanRequest.region(),
                createLoanRequest.state()
        );
        List<Installment> newInstallments = calculator.newInstallments(LoanTransformer.transformForNewInstallments(createLoanRequest));
        List<LoanInstallmentDto> loanInstallmentDtos = LoanInstallmentTransformer.transform(newInstallments, loanId);
        // Insert
        try {
            loanInstallmentDtos.forEach(
                    loanInstallmentDto ->
                            loanInstallmentDao.insert(loanInstallmentDto)
            );

        } catch (Exception e) {
            System.out.println(e);
        }
        loanDao.insert(loanDto);

        return loanId;
    }
}
