package com.corefin.server.v1;

import com.corefin.server.transform.LoanInstallmentTransformer;
import com.corefin.server.transform.LoanTransformer;
import com.corefin.server.v1.model.LoanInfo;
import com.corefin.server.v1.request.CreateLoanRequest;
import com.corefin.server.v1.response.GetLoanResponse;
import com.corefin.server.v1.response.GetLoansResponse;
import org.corefin.calculator.Actuarial365Calculator;
import org.corefin.calculator.model.Installment;
import org.corefin.dao.LoanDao;
import org.corefin.dao.LoanInstallmentDao;
import org.corefin.dto.LoanDto;
import org.corefin.dto.LoanInstallmentDto;
import org.corefin.model.common.LoanStatus;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class LoanResourceManager {
    private static final Logger LOGGER = Logger.getLogger(LoanResourceManager.class.getName());
    private final Actuarial365Calculator calculator;
    private final LoanDao loanDao;
    private final LoanInstallmentDao loanInstallmentDao;

    @Inject
    public LoanResourceManager(LoanDao loanDao,
                               LoanInstallmentDao loanInstallmentDao) {
        this.calculator = new Actuarial365Calculator();
        this.loanDao = loanDao;
        this.loanInstallmentDao = loanInstallmentDao;
    }

    public GetLoanResponse doGetLoan(String loanId) {
        LoanDto loanDto = loanDao.findById(loanId);
        List<LoanInstallmentDto> loanInstallmentDtoList = loanInstallmentDao.findByLoanId(loanId);

        return new GetLoanResponse(
                LoanTransformer.transformToLoanInfo(
                        loanDto,
                        loanInstallmentDtoList
                ));
    }

    public GetLoansResponse doGetLoans() {
        List<LoanDto> loanDtoList = loanDao.findAll();
        List<LoanInfo> loanInfolist = loanDtoList.stream().map(loanDto -> {
            String loanId = loanDto.loanId();
            List<LoanInstallmentDto> loanInstallmentDtoList = loanInstallmentDao.findByLoanId(loanId);
            return LoanTransformer.transformToLoanInfo(
                    loanDto,
                    loanInstallmentDtoList
            );
        }).collect(Collectors.toList());

        return new GetLoansResponse(
                loanInfolist
        );
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
                LoanStatus.CREATED,
                createLoanRequest.timezone(),
                createLoanRequest.region(),
                createLoanRequest.state()
        );
        List<Installment> newInstallments = calculator.newInstallments(LoanTransformer.transformForNewInstallments(createLoanRequest));
        List<LoanInstallmentDto> loanInstallmentDtos = LoanInstallmentTransformer.transform(newInstallments, loanId);
        // TODO(hubert): Add transactions
        loanInstallmentDtos.forEach(loanInstallmentDao::insert);
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
