package com.corefin.server.transform;

import com.corefin.server.v1.model.LoanInfo;
import com.corefin.server.v1.model.LoanInstallmentInfo;
import com.corefin.server.v1.request.CreateLoanRequest;
import org.corefin.calculator.model.Loan;
import org.corefin.dto.LoanDto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.Collections.emptyList;

public class LoanTransformer {

    public static Loan transformForNewInstallments(CreateLoanRequest createLoanRequest) {
        return new Loan(
                "",
                createLoanRequest.term(),
                createLoanRequest.originatedAmount(),
                createLoanRequest.currency(),
                createLoanRequest.targetInterestRate(),
                createLoanRequest.effectiveInterestRate(),
                createLoanRequest.startDate(),
                createLoanRequest.endDate(),
                createLoanRequest.timezone(),
                "",
                emptyList(),
                emptyList()
                );
    }

    public static LoanInfo transformToLoanInfo(LoanDto loanDto,
                                               List<LoanInstallmentInfo> loanInstallmentInfoList) {
        return new LoanInfo(
                loanDto.loanId(),
                loanDto.term(),
                loanDto.originatedAmount(),
                loanDto.currency(),
                loanDto.targetInterestRate(),
                loanDto.effectiveInterestRate(),
                loanDto.externalReference(),
                loanDto.startDate(),
                loanDto.endDate(),
                loanDto.status(),
                loanDto.timezone(),
                loanDto.region(),
                loanDto.state(),
                loanInstallmentInfoList
        );
    }
}
