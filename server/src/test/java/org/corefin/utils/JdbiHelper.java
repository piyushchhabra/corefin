package org.corefin.utils;

import org.apache.commons.dbcp2.BasicDataSource;
import org.jdbi.v3.core.Jdbi;
import javax.sql.DataSource;

public class JdbiHelper {
    private static DataSource dataSource() {

        BasicDataSource bds = new BasicDataSource();
        bds.setUsername("root");
        bds.setPassword("password");
        bds.setUrl("jdbc:mysql://localhost:3306/corefin_db");
        bds.addConnectionProperty("connectionTimeZone", "UTC");
        bds.setMaxTotal(-1);
        bds.setMinIdle(5);
        bds.setInitialSize(5);
        return bds;
    }

    public static Jdbi jdbi() {
        DataSource ds = dataSource();
        return Jdbi.create(ds);
    }
}