package com.corefin.server.v1;

import com.corefin.server.v1.request.CreateLoanRequest;
import org.corefin.dao.LoanDao;
import org.corefin.dao.LoanInstallmentDao;
import org.corefin.utils.JdbiHelper;
import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class LoanResourceTest {
    private LoanResourceManager loanResourceManager;
    private LoanDao loanDao;
    private LoanInstallmentDao loanInstallmentDao;

    @BeforeEach
    public void init() {
        Jdbi jdbi = JdbiHelper.jdbi();
        loanDao = new LoanDao(jdbi);
        loanInstallmentDao = new LoanInstallmentDao(jdbi);
        loanResourceManager = new LoanResourceManager(loanDao, loanInstallmentDao);
    }

//    @Test
    public void testCreateAndGetLoan() {
        int term = 10;
        BigDecimal originatedAmount = BigDecimal.valueOf(100.0);
        String currency = "USD";
        BigDecimal targetInterestRate = BigDecimal.valueOf(2.25);
        BigDecimal effectiveInterestRate = BigDecimal.valueOf(2.25);
        String externalReference = UUID.randomUUID().toString();
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now();
        String timezone = "America/Los_Angeles";
        String region = "USA";
        String state = "CA";

        CreateLoanRequest createLoanRequest = new CreateLoanRequest(
            term,
            originatedAmount,
            currency,
            targetInterestRate,
            effectiveInterestRate,
            externalReference,
            startDate,
            endDate,
            timezone,
            region,
            state
        );
        loanResourceManager.createLoan(createLoanRequest);
        // 1. CreateLoanRequest -> a new Loan object (testing api)
        // 2. Read the loan object (testing the dao)
    }
}

