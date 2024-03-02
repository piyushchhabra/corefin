package org.corefin.dao;

import org.corefin.JdbiHelper;
import org.corefin.dto.LoanDto;
import org.flywaydb.core.Flyway;
import org.h2.jdbcx.JdbcDataSource;
import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

public class LoanDaoTest {
    LoanDao loanDao;

    @BeforeEach
    public void init() {
        System.out.println("Migrations path: " + new File("migrations").getAbsolutePath());
        System.out.println("FUCK");
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setURL("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;MODE=MySQL");
        dataSource.setUser("sa");
        dataSource.setPassword("sa");
        Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations("filesystem:./migrations")
                .load();
        flyway.migrate();

        Jdbi jdbi = Jdbi.create(dataSource);

        loanDao = new LoanDao(jdbi);
    }

    @Test void testInsert() {
        int term = 10;
        BigDecimal originatedAmount = BigDecimal.valueOf(100.0);
        String currency = "USD";
        BigDecimal targetInterestRate = BigDecimal.valueOf(2.25);
        BigDecimal effectiveInterestRate = BigDecimal.valueOf(2.25);
        String externalReference = UUID.randomUUID().toString();
        ZonedDateTime startDate = ZonedDateTime.now();
        ZonedDateTime endDate = ZonedDateTime.now();
        String status = "NOT_STARTED";
        String timezone = "America/Los_Angeles";
        String region = "USA";
        String state = "CA";
        LoanDto dto = new LoanDto(
                null,
                term,
                originatedAmount,
                currency,
                targetInterestRate,
                effectiveInterestRate,
                externalReference,
                startDate,
                endDate,
                status,
                timezone,
                region,
                state
        );
        // loanDao.insert(dto);
    }
}
