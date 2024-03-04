package com.corefin.server.v1;

import com.corefin.server.v1.request.CreateLoanRequest;
import org.corefin.JdbiHelper;
import org.corefin.dao.LoanDao;
import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.UUID;

public class LoanResourceTest {
    LoanResourceManager loanResourceManager;
    LoanResource loanResource;

    @BeforeEach
    public void init() {
        Jdbi jdbi = JdbiHelper.jdbi();
        LoanDao loanDao = new LoanDao(jdbi);
        loanResourceManager = new LoanResourceManager(loanDao);
        loanResource = new LoanResource(loanResourceManager);
    }

    @Test
    public void testCreateAndGetLoan() {
        int term = 10;
        BigDecimal originatedAmount = BigDecimal.valueOf(100.0);
        String currency = "USD";
        BigDecimal targetInterestRate = BigDecimal.valueOf(2.25);
        BigDecimal effectiveInterestRate = BigDecimal.valueOf(2.25);
        String externalReference = UUID.randomUUID().toString();
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now();
        String status = "NOT_STARTED";
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
            status,
            timezone,
            region,
            state
        );
        // 1. CreateLoanRequest -> a new Loan object (testing api)
        // 2. Read the loan object (testing the dao)
    }
}

