package com.corefin.server.transform;

import com.corefin.server.v1.request.CreateLoanRequest;
import org.corefin.calculator.model.Loan;

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
}
