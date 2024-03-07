package com.corefin.server.v1;

import com.corefin.server.v1.request.MakePaymentRequest;
import org.corefin.calculator.Actuarial365Calculator;
import org.corefin.dao.LoanDao;
import org.corefin.dao.LoanInstallmentDao;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service
public class PaymentResourceManager {

    private Actuarial365Calculator calculator;
    private LoanDao loanDao;
    private LoanInstallmentDao loanInstallmentDao;

    @Inject
    public PaymentResourceManager(LoanDao loanDao,
                                  LoanInstallmentDao loanInstallmentDao) {
        this.calculator = new Actuarial365Calculator();
        this.loanDao = loanDao;
        this.loanInstallmentDao = loanInstallmentDao;
    }

    public String getPaymentInfo(String paymentId) {
        // paymentDao
        return "";
    }

    public String doMakePayment(String loanId, MakePaymentRequest makePaymentRequest) {
       return "";
    }

}
