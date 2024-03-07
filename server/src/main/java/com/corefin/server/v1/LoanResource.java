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
@Produces(MediaType.APPLICATION_JSON)
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
    public GetLoanResponse createLoan(@Valid CreateLoanRequest createLoanRequest) {
        return loanResourceManager.createLoan(createLoanRequest);
    }
    @GET
    @Path("/{loanId}")
    public GetLoanResponse getLoan(@PathParam("loanId") String loanId) {
        LOGGER.info("getLoan called for loan with id %s".formatted(loanId));
        return loanResourceManager.doGetLoan(loanId);
    }
}
