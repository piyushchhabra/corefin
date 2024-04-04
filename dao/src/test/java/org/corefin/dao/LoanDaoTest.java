package org.corefin.dao;

import org.corefin.dto.LoanDto;
import org.corefin.model.common.LoanStatus;
import org.flywaydb.core.Flyway;
import org.h2.jdbcx.JdbcDataSource;
import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LoanDaoTest {
    LoanDao loanDao;

    @BeforeEach
    public void init() {
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

    @Disabled
    @Test void testInsert() {
        String loanId = UUID.randomUUID().toString();
        int term = 10;
        BigDecimal originatedAmount = BigDecimal.valueOf(100.0);
        String currency = "USD";
        BigDecimal targetInterestRate = BigDecimal.valueOf(2.25);
        BigDecimal effectiveInterestRate = BigDecimal.valueOf(2.25);
        String externalReference = UUID.randomUUID().toString();
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now();
        LoanStatus status = LoanStatus.NOT_STARTED;
        String timezone = "America/Los_Angeles";
        String region = "USA";
        String state = "CA";
        LoanDto dto = new LoanDto(
                loanId,
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

        loanDao.insert(dto);
        LoanDto expectedLoanDto = loanDao.findById(loanId);
        assertEquals(loanId, expectedLoanDto.loanId());
    }
}
