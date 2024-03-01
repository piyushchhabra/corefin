package org.corefin.configuration;


import org.apache.commons.dbcp2.BasicDataSource;
import org.corefin.dao.LoanDao;
import org.jdbi.v3.core.Jdbi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class JdbiConfiguration {

    @Bean
    public DataSource dataSource(
            @Value("${database.username}") String dbUser,
            @Value("${database.password}") String dbPassword,
            @Value("${database.url}") String dbUrl,
            @Value("${database.timezone}") String connectionTimezone,
            @Value("${database.maxPooledConnections}") int maxPooledConnections,
            @Value("${database.minIdleConnections}") int minIdleConnections,
            @Value("${database.initialPooledConnections}") int intialPooledConnections
    ) {
        BasicDataSource bds = new BasicDataSource();
        bds.setUsername(dbUser);
        bds.setPassword(dbPassword);
        bds.setUrl(dbUrl);
        bds.addConnectionProperty("connectionTimeZone", connectionTimezone);
        bds.setMaxTotal(maxPooledConnections);
        bds.setMinIdle(minIdleConnections);
        bds.setInitialSize(intialPooledConnections);
        return bds;
    }
    @Bean
    public Jdbi jdbi(DataSource ds) {
        return Jdbi.create(ds);
    }

    @Bean
    public LoanDao loanDao(Jdbi db) {
        return new LoanDao(db);
    }

}
