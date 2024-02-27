package com.corefin.server;

import com.corefin.server.v1.response.GetLoanResponse;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import java.util.logging.Logger;

@Component
@Path("/loans")
public class LoanResource {

    private static final Logger LOGGER = Logger.getLogger(LoanResource.class.getName());
    private LoanResourceManager loanResourceManager;
    @Inject
    public LoanResource(LoanResourceManager loanResourceManager) {
        this.loanResourceManager = loanResourceManager;
    }
    @GET
    @Path("/{loanId}")
    public GetLoanResponse getLoan(@PathParam("loanId") String loanId) {
        LOGGER.info("getLoan called for loan with id %s".formatted(loanId));
        return loanResourceManager.doGetLoan(loanId);
    }
}
