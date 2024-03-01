package org.corefin.configuration;


import org.apache.commons.dbcp2.BasicDataSource;
import org.corefin.dao.LoanDao;
import org.jdbi.v3.core.Jdbi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class JdbiConfiguration {

    @Bean
    public DataSource dataSource() {
        BasicDataSource bs = new BasicDataSource();
        // TODO: Fill this out with our Mysql configuration
        return bs;
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
