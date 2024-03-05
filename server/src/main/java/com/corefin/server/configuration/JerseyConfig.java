package com.corefin.server.configuration;

//import com.corefin.server.exception.GenericExceptionMapper;
import com.corefin.server.exception.AppExceptionResolver;
import com.corefin.server.exception.BadArgumentExceptionMapper;
import com.corefin.server.v1.LoanResource;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JerseyConfig extends ResourceConfig {

    public JerseyConfig() {

        register(LoanResource.class);
        register(BadArgumentExceptionMapper.class);
        register(AppExceptionResolver.class);
    }

}
