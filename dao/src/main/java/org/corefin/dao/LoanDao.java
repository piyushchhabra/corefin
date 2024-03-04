package org.corefin.dao;

import org.corefin.dao.mappers.LoanMapper;
import org.corefin.dto.LoanDto;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.result.ResultBearing;
import org.jdbi.v3.core.statement.Update;

import javax.xml.transform.Result;
import java.sql.Statement;
import java.util.Optional;
import java.util.UUID;

// TODO: implement me
public class LoanDao implements BaseDao<LoanDto> {
    private Jdbi jdbi;
    public LoanDao(Jdbi jdbi) {
        this.jdbi = jdbi;
        registerRowMapper();
    }
    // TODO: Pass in Handle here
    @Override
    public LoanDto insert(LoanDto dto) {
        return jdbi.withHandle(
                handle -> insert(dto, handle)
        );
    }
    public LoanDto insert(LoanDto dto, Handle handle) {
        String insertQuery = """
                INSERT INTO loan (loan_id,
                                  term,
                                  originated_amount,
                                  currency,
                                  target_interest_rate,
                                  effective_interest_rate,
                                  external_reference,
                                  start_date,
                                  end_date,
                                  status,
                                  timezone,
                                  region,
                                  state)
                VALUES (
                        :loan_id,
                        :term,
                        :originated_amount,
                        :currency,
                        :target_interest_rate,
                        :effective_interest_rate,
                        :external_reference,
                        :start_date,
                        :end_date,
                        :status,
                        :timezone,
                        :region,
                        :state)
                """;
        // TODO: check if row count == 0 -> that means an exception happened
        handle.createUpdate(insertQuery)
                .bind("loan_id", dto.loanId())
                .bind("term", dto.term())
                .bind("originated_amount", dto.originatedAmount())
                .bind("currency", dto.currency())
                .bind("target_interest_rate", dto.targetInterestRate())
                .bind("effective_interest_rate", dto.effectiveInterestRate())
                .bind("external_reference", dto.externalReference())
                .bind("start_date", dto.startDate())
                .bind("end_date", dto.endDate())
                .bind("status", dto.status())
                .bind("timezone", dto.timezone())
                .bind("region", dto.region())
                .bind("state", dto.state())
                .execute();
        return findById(dto.loanId());
    }

    @Override
    public LoanDto findById(String loanId) {
        return jdbi.withHandle(
                handle -> handle.createQuery("SELECT * FROM loan WHERE loan_id = :loan_id")
                        .bind("loan_id", loanId)
                        .mapTo(LoanDto.class)
                        .findOne()
                        .orElse(null)
        );
    }
    @Override
    public void registerRowMapper() {
        jdbi.registerRowMapper(new LoanMapper());
    }
}
