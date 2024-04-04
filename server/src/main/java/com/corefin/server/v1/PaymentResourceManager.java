package com.corefin.server.v1;

import com.corefin.server.exception.CorefinException;
import com.corefin.server.transform.LoanTransformer;
import com.corefin.server.v1.request.MakePaymentRequest;
import com.corefin.server.v1.response.GetLoanResponse;
import com.corefin.server.v1.response.GetPaymentResponse;
import com.corefin.server.v1.response.GetPaymentsResponse;
import org.corefin.calculator.Actuarial365Calculator;
import org.corefin.calculator.model.Installment;
import org.corefin.calculator.model.Loan;
import org.corefin.calculator.model.Payment;
import org.corefin.dao.LoanDao;
import org.corefin.dao.LoanInstallmentDao;
import org.corefin.dao.PaymentDao;
import org.corefin.dto.LoanDto;
import org.corefin.dto.LoanInstallmentDto;
import org.corefin.dto.PaymentDto;
import org.corefin.model.common.InstallmentStatus;
import org.corefin.model.common.PaymentType;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

@Service
public class PaymentResourceManager {
    private static final Logger LOGGER = Logger.getLogger(PaymentResourceManager.class.getName());
    private final Actuarial365Calculator calculator;
    private final LoanDao loanDao;
    private final LoanInstallmentDao loanInstallmentDao;
    private final PaymentDao paymentDao;
    private final LoanResourceManager loanResourceManager;

    @Inject
    public PaymentResourceManager(LoanDao loanDao,
                                  LoanInstallmentDao loanInstallmentDao,
                                  PaymentDao paymentDao,
                                  @Lazy LoanResourceManager loanResourceManager) {
        this.calculator = new Actuarial365Calculator();
        this.loanDao = loanDao;
        this.loanInstallmentDao = loanInstallmentDao;
        this.paymentDao = paymentDao;
        this.loanResourceManager = loanResourceManager;
    }

    // TODO: Handle off-cycle payments
    // TODO: do calculator integration for early, late payments
    // TODO: Run updateInstallments.
    // TODO: Compute outstandingPrincipal, check if it's == 0 then close the loan.
    // TODO: Pin to Loan's zone time (paymentDto)
    // TODO: Transactions
    /**
     * Applies the payment and updates the state of the installments for the
     * associated Loan.
     */
    public GetLoanResponse doMakePayment(MakePaymentRequest makePaymentRequest) {
        String loanId = makePaymentRequest.loanId();
        LOGGER.info("Making payment to loanId %s with %s".formatted(loanId, makePaymentRequest));
        validateMakePaymentRequest(makePaymentRequest);
        List<LoanInstallmentDto> loanInstallmentDtoList = loanInstallmentDao.findByLoanId(loanId);
        Optional<LoanInstallmentDto> firstUnpaidInstallmentOptional =
                loanInstallmentDtoList.stream()
                        .filter(c -> c.status() == InstallmentStatus.OWED)
                        .findFirst();
        if (firstUnpaidInstallmentOptional.isEmpty()) {
            throw new CorefinException("No unpaid installments");
        }
        LoanInstallmentDto firstUnpaidInstallment = firstUnpaidInstallmentOptional.get();
        //validateMakePaymentRequestDate(makePaymentRequest, firstUnpaidInstallment);
        BigDecimal installmentAmount = firstUnpaidInstallment.interestAmount()
                .add(firstUnpaidInstallment.principalAmount());
        //validateMakePaymentRequestAmount(installmentAmount, makePaymentRequest.amount());
        String paymentId = UUID.randomUUID().toString();
        PaymentDto paymentDto = new PaymentDto(
                paymentId,
                loanId,
                makePaymentRequest.amount(),
                PaymentType.valueOf(makePaymentRequest.paymentType()),
                makePaymentRequest.paymentDateTime()
        );

        paymentDao.insert(paymentDto);

        // Convert LoanDao to Loan
        LoanDto loanDto = loanDao.findById(loanId);
        List<PaymentDto> paymentDtoList = paymentDao.findByLoanId(loanId);
        Loan loan = LoanTransformer.transform(loanDto, paymentDtoList);

        // Call the calculator's updateInstallments with Loan, calculation date = now()
        Loan updatedLoan = calculator.updateInstallments(loan, LocalDate.now());
        List<Installment> installments = updatedLoan.installments();
        LOGGER.info("Generated updated installments %s".formatted(installments));

        // For each installment in the updated loan, update the associated LoanInstallment
        ArrayList<LoanInstallmentDto> updatedLoanInstallmentDtos = new ArrayList<>();

        // Reconcile the loanInstallmentDtoList with calculator's installments
        for (int i = 0; i < loanInstallmentDtoList.size(); i++) {
            LoanInstallmentDto originalLoanInstallmentDto = loanInstallmentDtoList.get(i);
            Installment updatedInstallment = installments.get(i);
            updatedLoanInstallmentDtos.add(
                    new LoanInstallmentDto(
                            originalLoanInstallmentDto.installmentId(),
                            updatedInstallment.status(),
                            loanId,
                            updatedInstallment.numTerm(),
                            updatedInstallment.principalAmount(),
                            updatedInstallment.interestAmount(),
                            originalLoanInstallmentDto.startDate(),
                            originalLoanInstallmentDto.dueDate(),
                            originalLoanInstallmentDto.endDate()
                    )
            );
        }

        updatedLoanInstallmentDtos.forEach(loanInstallmentDao::updateInstallmentForPayment);
        return loanResourceManager.doGetLoan(loanId);
    }

    /**
     * Gets payment by ID
     *
     * @param paymentId
     * @return
     */
    public GetPaymentResponse doGetPayment(String paymentId) {
        PaymentDto paymentDto = paymentDao.findById(paymentId);
        return new GetPaymentResponse(paymentDto);
    }

    /**
     * Gets all payments that have been applied to a Loan.
     *
     * @param loanId
     * @return
     */
    public GetPaymentsResponse doGetPaymentsForLoan(String loanId) {
        List<PaymentDto> paymentDtoList = paymentDao.findByLoanId(loanId);
        return new GetPaymentsResponse(paymentDtoList);
    }

    private void validateMakePaymentRequestDate(MakePaymentRequest makePaymentRequest,
                                                LoanInstallmentDto firstUnpaidInstallment) {
        if (!makePaymentRequest.paymentDateTime().toLocalDate().equals(firstUnpaidInstallment.dueDate())) {
            LOGGER.severe("Payment date %s and due date %s don't match. Your version of" +
                    "Corefin currently only supports on-time payments.".formatted(
                            makePaymentRequest.paymentDateTime(),
                            firstUnpaidInstallment.dueDate()
                    ));
            throw new CorefinException("Payment amount doesn't equal installment amount");
        }
    }
    private void validateMakePaymentRequestAmount(BigDecimal installmentAmount,
                                                  BigDecimal paymentAmount) {
        if (installmentAmount.compareTo(paymentAmount) != 0) {
            LOGGER.severe("Installment amount doesn't match with payment amount. " +
                    "Installment amount %s. Payment Amount: %s".formatted(installmentAmount, paymentAmount));
            throw new CorefinException("Payment amount doesn't equal installment amount");
        }
    }

    private void validateMakePaymentRequest(MakePaymentRequest makePaymentRequest) {
        // Validate payment type
        try {
            PaymentType.valueOf(makePaymentRequest.paymentType());
        } catch (IllegalArgumentException e) {
            LOGGER.severe("Invalid payment type " + makePaymentRequest.paymentType());
            throw new CorefinException("Invalid payment type %s".formatted(makePaymentRequest.paymentType()), e);
        }

        // Validate loan exists
        try {
            loanDao.findById(makePaymentRequest.loanId());
        } catch (Exception e) {
            LOGGER.severe("Could not find loan with id " + makePaymentRequest.loanId());
            throw new CorefinException("Invalid loan id %s".formatted(makePaymentRequest.loanId()), e);
        }
    }
}