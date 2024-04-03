package com.corefin.server.v1;

import com.corefin.server.v1.model.LoanInstallmentInfo;
import com.corefin.server.v1.request.CreateLoanRequest;
import com.corefin.server.v1.response.GetLoanResponse;
import org.corefin.dao.LoanDao;
import org.corefin.dao.LoanInstallmentDao;
import org.corefin.model.common.InstallmentStatus;
import org.corefin.utils.JdbiHelper;
import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LoanResourceManagerTest {
    private LoanResourceManager loanResourceManager;
    private LoanDao loanDao;
    private LoanInstallmentDao loanInstallmentDao;

    /**
     * Sets up the test fixture.
     * (Called before every test case method.)
     */
    @BeforeEach
    public void init() {
        Jdbi jdbi = JdbiHelper.jdbi();
        loanDao = new LoanDao(jdbi);
        loanInstallmentDao = new LoanInstallmentDao(jdbi);
        loanResourceManager = new LoanResourceManager(loanDao, loanInstallmentDao);
    }

    /**
     * Tests the loan creation functionality of the API for an interest bearing
     * term loan.
     */
    @Test
    public void testCreateLoan_InterestBearing() {
        int term = 4;
        BigDecimal originatedAmount = BigDecimal.valueOf(100.0);
        String currency = "USD";
        BigDecimal targetInterestRate = BigDecimal.valueOf(0.1);
        BigDecimal effectiveInterestRate = BigDecimal.valueOf(0.1);
        String externalReference = UUID.randomUUID().toString();
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = startDate;
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

        GetLoanResponse loanResponse = loanResourceManager.createLoan(createLoanRequest);

        // Verify that all expected fields are set correctly in the response
        assertEquals(loanResponse.loanInfo().term(), term);
        assertEquals(loanResponse.loanInfo().targetInterestRate(), targetInterestRate);
        assertEquals(loanResponse.loanInfo().effectiveInterestRate(), effectiveInterestRate);
        assertEquals(loanResponse.loanInfo().currency(), currency);
        assertEquals(loanResponse.loanInfo().externalReference(), externalReference);
        assertEquals(loanResponse.loanInfo().startDate(), startDate);
        assertEquals(loanResponse.loanInfo().endDate(), endDate);
        assertEquals(loanResponse.loanInfo().timezone(), timezone);
        assertEquals(loanResponse.loanInfo().region(), region);
        assertEquals(loanResponse.loanInfo().state(), state);

        // Verify that the installment schedule is correct
        List<LoanInstallmentInfo> loanInstallmentInfoList = loanResponse.loanInfo().loanInstallments();
        assertEquals(loanInstallmentInfoList.size(), 4);

        loanInstallmentInfoList.forEach(loanInstallmentInfo -> {
            assertEquals(loanInstallmentInfo.loanId(), loanResponse.loanInfo().loanId());
            assertEquals(loanInstallmentInfo.status(), InstallmentStatus.OWED);
        });

        LoanInstallmentInfo installment1 = loanInstallmentInfoList.get(0);
        assertEquals(installment1.principalAmount(), BigDecimal.valueOf(24.67));
        assertEquals(installment1.interestAmount(), BigDecimal.valueOf(0.85));

        LoanInstallmentInfo installment2 = loanInstallmentInfoList.get(1);
        assertEquals(installment2.principalAmount(), BigDecimal.valueOf(24.94));
        assertEquals(installment2.interestAmount(), BigDecimal.valueOf(0.58));

        LoanInstallmentInfo installment3 = loanInstallmentInfoList.get(2);
        assertEquals(installment3.principalAmount(), BigDecimal.valueOf(25.09));
        assertEquals(installment3.interestAmount(), BigDecimal.valueOf(0.43));

        LoanInstallmentInfo installment4 = loanInstallmentInfoList.get(3);
        assertEquals(installment4.principalAmount(), BigDecimal.valueOf(25.3).setScale(2));
        assertEquals(installment4.interestAmount(), BigDecimal.valueOf(0.21));
    }

    /**
     * Tests the loan creation functionality of the API for a non-interest bearing (BNPL)
     * term loan.
     */
    @Test
    public void testCreateLoan_BNPL() {
        int term = 4;
        BigDecimal originatedAmount = BigDecimal.valueOf(100.0);
        String currency = "USD";
        String externalReference = UUID.randomUUID().toString();
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = startDate;
        String timezone = "America/Los_Angeles";
        String region = "USA";
        String state = "CA";

        CreateLoanRequest createLoanRequest = new CreateLoanRequest(
                term,
                originatedAmount,
                currency,
                BigDecimal.valueOf(0),
                BigDecimal.valueOf(0),
                externalReference,
                startDate,
                endDate,
                timezone,
                region,
                state
        );

        GetLoanResponse loanResponse = loanResourceManager.createLoan(createLoanRequest);

        // Verify that all expected fields are set correctly in the response
        assertEquals(loanResponse.loanInfo().term(), term);
        assertEquals(loanResponse.loanInfo().targetInterestRate(), BigDecimal.valueOf(0));
        assertEquals(loanResponse.loanInfo().effectiveInterestRate(), BigDecimal.valueOf(0));
        assertEquals(loanResponse.loanInfo().currency(), currency);
        assertEquals(loanResponse.loanInfo().externalReference(), externalReference);
        assertEquals(loanResponse.loanInfo().startDate(), startDate);
        assertEquals(loanResponse.loanInfo().endDate(), endDate);
        assertEquals(loanResponse.loanInfo().timezone(), timezone);
        assertEquals(loanResponse.loanInfo().region(), region);
        assertEquals(loanResponse.loanInfo().state(), state);

        // Verify that the installment schedule is correct
        List<LoanInstallmentInfo> loanInstallmentInfoList = loanResponse.loanInfo().loanInstallments();
        assertEquals(loanInstallmentInfoList.size(), 4);

        // Each installment's principal should be $100/4 = $25 and its interest should be $0
        for (int i = 0; i < 4; i++) {
            LoanInstallmentInfo installment = loanInstallmentInfoList.get(i);
            assertEquals(installment.loanId(), loanResponse.loanInfo().loanId());
            assertEquals(installment.status(), InstallmentStatus.OWED);
            assertEquals(installment.principalAmount(), BigDecimal.valueOf(25).setScale(2));
            assertEquals(installment.interestAmount(), BigDecimal.valueOf(0).setScale(2));
        }
    }
}
