package com.corefin.server.v1;

import com.corefin.server.v1.request.CreateLoanRequest;
import com.corefin.server.v1.response.GetInstallmentsResponse;
import com.corefin.server.v1.response.GetLoanResponse;
import com.corefin.server.v1.response.GetLoansResponse;
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

    private final LoanResourceManager loanResourceManager;
    private final PaymentResourceManager paymentResourceManager;
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
    public GetLoansResponse getLoans() {
        LOGGER.info("getLoans called");
        return loanResourceManager.doGetLoans();
    }

    @GET
    @Path("/{loanId}")
    public GetLoanResponse getLoan(@PathParam("loanId") String loanId) {
        LOGGER.info("getLoan called for loan with id %s".formatted(loanId));
        return loanResourceManager.doGetLoan(loanId);
    }

    @GET
    @Path("/{loanId}/installments/pastDue")
    public GetInstallmentsResponse getPastDueInstallments(@PathParam("loanId") String loanId) {
        LOGGER.info("getPastDueInstallments called for loan with id %s".formatted(loanId));
        return loanResourceManager.doGetPastDueInstallments(loanId);
    }
}
