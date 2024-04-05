package com.corefin.server.v1;

import com.corefin.server.v1.request.MakePaymentRequest;
import com.corefin.server.v1.response.GetLoanResponse;
import com.corefin.server.v1.response.GetPaymentResponse;
import com.corefin.server.v1.response.GetPaymentsResponse;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.logging.Logger;

/*
 * Payment Resource to handle all payment related apis.
 * TODOs:
 * - Implement /refund suite
 * - Implement /reversals
 * - Reopen a loan when a reversal has taken place.
 * - Refund should be able to close a loan
 */
@Component
@Path("/payments")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PaymentResource {

    private static final Logger LOGGER = Logger.getLogger(PaymentResource.class.getName());

    private final PaymentResourceManager paymentResourceManager;

    @Inject
    public PaymentResource(PaymentResourceManager paymentResourceManager) {
        this.paymentResourceManager = paymentResourceManager;
    }

    @POST
    public GetLoanResponse makePayment(@Valid MakePaymentRequest makePaymentRequest) {
        LOGGER.info("makePayment called for loan with id %s".formatted(makePaymentRequest.loanId()));
        return paymentResourceManager.doMakePayment(makePaymentRequest);
    }

    @GET
    public GetPaymentsResponse getPayments() {
        LOGGER.info("getPayments called");
        return paymentResourceManager.doGetPayments();
    }

    @GET
    @Path("/{paymentId}")
    public GetPaymentResponse getPayment(@PathParam("paymentId") String paymentId) {
        LOGGER.info("getPayment called for payment with id %s".formatted(paymentId));
        return paymentResourceManager.doGetPayment(paymentId);
    }

    @GET
    @Path("/loan/{loanId}")
    public GetPaymentsResponse getPaymentsForLoan(@PathParam("loanId") String loanId) {
        LOGGER.info("getPaymentsForLoan called for loan with id %s".formatted(loanId));
        return paymentResourceManager.doGetPaymentsForLoan(loanId);
    }
}
