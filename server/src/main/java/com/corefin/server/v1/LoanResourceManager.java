package com.corefin.server.v1;

import com.corefin.server.transform.LoanInstallmentTransformer;
import com.corefin.server.transform.LoanTransformer;
import com.corefin.server.v1.model.LoanInfo;
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
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

@Service
public class LoanResourceManager {
    private static final Logger LOGGER = Logger.getLogger(PaymentResourceManager.class.getName());
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
        LoanDto loanDto = loanDao.findById(loanId);
        List<LoanInstallmentDto> loanInstallmentDtos = loanInstallmentDao.findByLoanId(loanId);

        return new GetLoanResponse(
                LoanTransformer.transformToLoanInfo(
                        loanDto,
                        loanInstallmentDtos
                ));
    }

    public GetLoanResponse createLoan(CreateLoanRequest createLoanRequest) {
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
                "CREATED",
                createLoanRequest.timezone(),
                createLoanRequest.region(),
                createLoanRequest.state()
        );
        List<Installment> newInstallments = calculator.newInstallments(LoanTransformer.transformForNewInstallments(createLoanRequest));
        List<LoanInstallmentDto> loanInstallmentDtos = LoanInstallmentTransformer.transform(newInstallments, loanId);
        // TODO(hubert): Add transactionals
        loanInstallmentDtos.forEach(
                loanInstallmentDto ->
                        loanInstallmentDao.insert(loanInstallmentDto)
        );
        loanDao.insert(loanDto);
        loanInstallmentDtos = loanInstallmentDao.findByLoanId(loanId);
        LOGGER.info("Creating new Loan: %s\nInstallments: %s".formatted(loanDto, loanInstallmentDtos));

        return new GetLoanResponse(
                LoanTransformer.transformToLoanInfo(
                        loanDto,
                        loanInstallmentDtos
                ));

    }
}
