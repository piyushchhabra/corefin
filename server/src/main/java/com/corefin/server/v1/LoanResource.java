package com.corefin.server.v1;

import com.corefin.server.v1.request.CreateLoanRequest;
import com.corefin.server.v1.request.MakePaymentRequest;
import com.corefin.server.v1.response.GetLoanResponse;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.logging.Logger;

@Component
@Path("/loans")
@Consumes(MediaType.APPLICATION_JSON)
public class LoanResource {

    private static final Logger LOGGER = Logger.getLogger(LoanResource.class.getName());

    private LoanResourceManager loanResourceManager;
    private PaymentResourceManager paymentResourceManager;
    @Inject
    public LoanResource(LoanResourceManager loanResourceManager,
                        PaymentResourceManager paymentResourceManager) {
        this.loanResourceManager = loanResourceManager;
        this.paymentResourceManager = paymentResourceManager;
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public GetLoanResponse createLoan(@Valid CreateLoanRequest createLoanRequest) {
        return loanResourceManager.createLoan(createLoanRequest);
    }
    @GET
    @Path("/{loanId}")
    @Produces(MediaType.APPLICATION_JSON)
    public GetLoanResponse getLoan(@PathParam("loanId") String loanId) {
        LOGGER.info("getLoan called for loan with id %s".formatted(loanId));
        return loanResourceManager.doGetLoan(loanId);
    }
//
//
//
//    @GET
//    @Path("/payment/{paymentId}")
//    @Produces(MediaType.APPLICATION_JSON)
//    public String getPaymentInfo(@PathParam("paymentId") String paymentId) {
//        LOGGER.info("getPaymentInfo called for payment with id %s".formatted(paymentId));
//        return paymentResourceManager.getPaymentInfo(paymentId);
//    }
//
    @POST
    @Path("/{loanId}/makePayment")
//    @Produces(MediaType.TEXT_PLAIN)
    public String makePayment(@PathParam("loanId") String loanId,
                              @Valid MakePaymentRequest makePaymentRequest) {
        LOGGER.info("makePayment called for loan with id %s".formatted(loanId));
        return paymentResourceManager.doMakePayment(loanId, makePaymentRequest);
    }
}
