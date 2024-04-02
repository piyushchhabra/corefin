package com.corefin.server.transform;

import com.corefin.server.v1.model.LoanInfo;
import com.corefin.server.v1.model.LoanInstallmentInfo;
import com.corefin.server.v1.request.CreateLoanRequest;
import org.corefin.calculator.model.Loan;
import org.corefin.calculator.model.Payment;
import org.corefin.dao.LoanDao;
import org.corefin.dto.LoanDto;
import org.corefin.dto.LoanInstallmentDto;
import org.corefin.dto.PaymentDto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
                createLoanRequest.timezone(),
                "",
                // Unused for new Installments
                new ArrayList<>(),
                new ArrayList<>(),
                BigDecimal.ZERO,
                BigDecimal.ZERO
                );
    }

    public static Loan transform(LoanDto loanDto, List<PaymentDto> paymentDtos) {
        List<Payment> payments = paymentDtos.stream()
                .map(PaymentTransformer::transform)
                .collect(Collectors.toList());
        return new Loan(
                "",
                loanDto.term(),
                loanDto.originatedAmount(),
                loanDto.currency(),
                loanDto.targetInterestRate(),
                loanDto.effectiveInterestRate(),
                loanDto.startDate(),
                loanDto.timezone(),
                "",
                new ArrayList<>(payments),
                new ArrayList<>(),
                BigDecimal.ZERO,
                BigDecimal.ZERO
        );
    }

    public static LoanInfo transformToLoanInfo(LoanDto loanDto,
                                               List<LoanInstallmentDto> loanInstallmentInfoList) {
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
                LoanInstallmentTransformer.transform(loanInstallmentInfoList)
        );
    }
}
