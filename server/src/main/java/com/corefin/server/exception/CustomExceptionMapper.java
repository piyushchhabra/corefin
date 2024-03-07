package com.corefin.server.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import java.util.logging.Logger;

public class CustomExceptionMapper implements ExceptionMapper<Throwable> {
    private static final Logger LOGGER = Logger.getLogger(CustomExceptionMapper.class.getName());
    @Override
    public Response toResponse(Throwable exception) {
        LOGGER.severe("Caught an exception: " + exception.getMessage());
        return Response.status(500).entity(exception).build();
    }
}
