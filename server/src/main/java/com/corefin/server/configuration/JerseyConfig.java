package com.corefin.server.configuration;

import com.corefin.server.LoanResource;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;

@Component
public class JerseyConfig extends ResourceConfig {

    public JerseyConfig() {
        register(LoanResource.class);
    }

}
