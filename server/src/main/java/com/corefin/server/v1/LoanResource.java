package com.corefin.server.v1;

import com.corefin.server.configuration.JerseyConfig;
import com.corefin.server.v1.response.GetLoanResponse;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.logging.Logger;

@Component
@Path("/loans")
@Consumes(MediaType.APPLICATION_JSON)
public class LoanResource {

    private static final Logger LOGGER = Logger.getLogger(LoanResource.class.getName());
    private LoanResourceManager loanResourceManager;
    @Inject
    public LoanResource(LoanResourceManager loanResourceManager) {
        this.loanResourceManager = loanResourceManager;
    }
    @GET
    @Path("/{loanId}")
    @Produces(MediaType.APPLICATION_JSON)
    public GetLoanResponse getLoan(@PathParam("loanId") String loanId) {
        LOGGER.info("getLoan called for loan with id %s".formatted(loanId));
        return new GetLoanResponse(loanId);
    }
}
