package com.corefin.server.transform;

import org.corefin.calculator.model.Installment;
import org.corefin.calculator.model.InstallmentStatus;
import org.corefin.dto.LoanDto;
import org.corefin.dto.LoanInstallmentDto;

import java.util.List;

public class LoanInstallmentTransformer {
    public static LoanInstallmentDto transform(Installment installment, String loanId) {
        return new LoanInstallmentDto(
                installment.installmentId(),
                loanId,
                installment.numTerm(),
                installment.principalAmount(),
                installment.interestAmount(),
                installment.startDate(),
                installment.dueDate(),
                installment.endDate(),
                installment.status()
        );
    }

    public static List<LoanInstallmentDto> transform(List<Installment> installments,
                                                     String loanId) {
        return installments.stream()
                .map(installment ->
                        transform(installment,
                                  loanId))
                .toList();
    }
}
