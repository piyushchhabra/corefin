package com.corefin.server.v1;

import com.corefin.server.v1.request.MakePaymentRequest;
import com.corefin.server.v1.response.GetLoanResponse;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
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

    private PaymentResourceManager paymentResourceManager;
    @Inject
    public PaymentResource(PaymentResourceManager paymentResourceManager) {
        this.paymentResourceManager = paymentResourceManager;
    }

    @POST
    @Path("{loanId}/makePayment")
    public GetLoanResponse makePayment(@PathParam("loanId") String loanId,
                                       @Valid MakePaymentRequest makePaymentRequest) {
        LOGGER.info("makePayment called for loan with id %s".formatted(loanId));
        return paymentResourceManager.doMakePayment(loanId, makePaymentRequest);
    }
}
