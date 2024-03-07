package com.corefin.server.configuration;

//import com.corefin.server.exception.AppExceptionResolver;
//import com.corefin.server.exception.BadArgumentExceptionMapper;
//import com.corefin.server.exception.CustomExceptionMapper;

import com.corefin.server.exception.CustomExceptionMapper;
import com.corefin.server.v1.LoanResource;
import com.corefin.server.v1.PaymentResource;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CustomJerseyConfig extends ResourceConfig {

    public CustomJerseyConfig() {
        register(LoanResource.class);
        register(PaymentResource.class);
        register(CustomExceptionMapper.class);
    }

}
