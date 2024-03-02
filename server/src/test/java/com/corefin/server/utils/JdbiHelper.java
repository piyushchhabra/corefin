package com.corefin.server.utils;


import com.corefin.server.v1.LoanResourceManager;
import org.apache.commons.dbcp2.BasicDataSource;
import org.corefin.dao.LoanDao;
import org.jdbi.v3.core.Jdbi;

import javax.sql.DataSource;

public class JdbiHelper {
    private static DataSource dataSource() {
        BasicDataSource bds = new BasicDataSource();
        bds.setUsername("admin");
        bds.setPassword("password");
        bds.setUrl("jdbc:mysql://localhost:3306");
        bds.addConnectionProperty("connectionTimeZone", "UTC");
        bds.setMaxTotal(-1);
        bds.setMinIdle(5);
        bds.setInitialSize(5);
        return bds;
    }

    private static Jdbi jdbi() {
        DataSource ds = dataSource();
        return Jdbi.create(ds);
    }

    private static LoanDao loanDao() {
        Jdbi db = jdbi();
        return new LoanDao(db);
    }

    public static LoanResourceManager loanResourceManager() {
       LoanDao loanDao = loanDao();
       return new LoanResourceManager(loanDao);
    }
}
