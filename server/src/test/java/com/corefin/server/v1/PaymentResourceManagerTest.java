package com.corefin.server.v1;

import com.corefin.server.exception.CorefinException;
import com.corefin.server.v1.model.LoanInfo;
import com.corefin.server.v1.model.LoanInstallmentInfo;
import com.corefin.server.v1.request.CreateLoanRequest;
import com.corefin.server.v1.request.MakePaymentRequest;
import com.corefin.server.v1.response.GetLoanResponse;
import org.corefin.dao.LoanDao;
import org.corefin.dao.LoanInstallmentDao;
import org.corefin.dao.PaymentDao;
import org.corefin.model.common.InstallmentStatus;
import org.corefin.model.common.PaymentType;
import org.corefin.utils.JdbiHelper;
import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.*;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PaymentResourceManagerTest {
    private LoanDao loanDao;
    private LoanInstallmentDao loanInstallmentDao;
    private PaymentDao paymentDao;
    private LoanResourceManager loanResourceManager;
    private PaymentResourceManager paymentResourceManager;
    private final ZoneId zoneId = ZoneId.of("America/Los_Angeles");

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
        paymentDao = new PaymentDao(jdbi);
        paymentResourceManager = new PaymentResourceManager(loanDao, loanInstallmentDao, paymentDao, loanResourceManager);
    }

    private LoanInfo initLoan() {
        int term = 4;
        BigDecimal originatedAmount = BigDecimal.valueOf(100.0);
        String currency = "USD";
        BigDecimal targetInterestRate = BigDecimal.valueOf(5);
        BigDecimal effectiveInterestRate = BigDecimal.valueOf(5);
        String externalReference = UUID.randomUUID().toString();
        LocalDate startDate = LocalDate.of(2024, Month.JANUARY, 1);
        LocalDate endDate = LocalDate.of(2024, Month.MAY, 1);;
        String timezone = zoneId.toString();
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
        return loanResourceManager.createLoan(createLoanRequest).loanInfo();
    }

    /**
     * Tests the basic functionality of applying a single on-time payment.
     */
    @Test
    public void testDoMakePayment() {
        LoanInfo loanInfo = initLoan();
        BigDecimal principalAmount = BigDecimal.valueOf(14.33);
        BigDecimal interestAmount = BigDecimal.valueOf(41.10);

        ZonedDateTime paymentDate =
                ZonedDateTime.of(loanInfo.startDate().plusMonths(1), LocalTime.MIDNIGHT, zoneId);

        MakePaymentRequest makePaymentRequest = new MakePaymentRequest(
                loanInfo.loanId(),
                principalAmount.add(interestAmount),
                PaymentType.PAYMENT.toString(),
                paymentDate
        );

        GetLoanResponse loanResponse = paymentResourceManager.doMakePayment(makePaymentRequest);
        List<LoanInstallmentInfo> loanInstallmentInfoList =
                loanResponse.loanInfo().loanInstallments();

        LoanInstallmentInfo firstInstallment = loanInstallmentInfoList.get(0);
        assertEquals(firstInstallment.status(), InstallmentStatus.PAID);
    }

    /**
     * Tests the PaymentResourceManager's behavior when a payment is applied with an invalid
     * payment type.
     */
    @Test
    public void testDoMakePayment_InvalidPaymentType() {
        LoanInfo loanInfo = initLoan();
        MakePaymentRequest invalidMakePaymentRequest = new MakePaymentRequest(
                loanInfo.loanId(),
                BigDecimal.valueOf(0),
                "INVALID_PAYMENT_TYPE",
                ZonedDateTime.of(loanInfo.startDate().plusMonths(1), LocalTime.MIDNIGHT, zoneId)
        );
        CorefinException exception = assertThrows(CorefinException.class, () -> {
            paymentResourceManager.doMakePayment(invalidMakePaymentRequest);
        });
        assertEquals(exception.getMessage(), "Invalid payment type INVALID_PAYMENT_TYPE");
    }

    /**
     * Tests the PaymentResourceManager's behavior when a payment is applied with an invalid
     * loan id.
     */
    @Test
    public void testDoMakePayment_LoanDoesNotExist() {
        LoanInfo loanInfo = initLoan();
        MakePaymentRequest invalidMakePaymentRequest = new MakePaymentRequest(
                "INVALID_LOAN_ID",
                BigDecimal.valueOf(0),
                PaymentType.PAYMENT.toString(),
                ZonedDateTime.of(loanInfo.startDate().plusMonths(1), LocalTime.MIDNIGHT, zoneId)
        );
        CorefinException exception = assertThrows(CorefinException.class, () -> {
            paymentResourceManager.doMakePayment(invalidMakePaymentRequest);
        });
        assertEquals(exception.getMessage(), "Invalid loan id INVALID_LOAN_ID");
    }
}
