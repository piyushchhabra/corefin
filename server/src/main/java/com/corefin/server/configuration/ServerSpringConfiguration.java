package com.corefin.server.configuration;

import org.corefin.configuration.JdbiConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

@Component
@Configuration
@Import(JdbiConfiguration.class)
public class ServerSpringConfiguration {
    @Configuration
    public class RequestLoggingFilterConfig {

        @Bean
        public CommonsRequestLoggingFilter logFilter() {
            CommonsRequestLoggingFilter filter
                    = new CommonsRequestLoggingFilter();
            filter.setIncludeQueryString(true);
            filter.setIncludePayload(true);
            filter.setMaxPayloadLength(10000);
            filter.setIncludeHeaders(false);
            filter.setAfterMessagePrefix("REQUEST DATA: ");
            return filter;
        }
    }
}
