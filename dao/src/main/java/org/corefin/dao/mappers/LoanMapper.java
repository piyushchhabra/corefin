package org.corefin.dao.mappers;


import org.corefin.dto.LoanDto;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneId;
import java.util.Currency;

// Converts the raw database object ResultSet into the LoanDto object
public class LoanMapper implements RowMapper<LoanDto> {

    @Override
    public LoanDto map(ResultSet rs, StatementContext ctx) throws SQLException {
        return new LoanDto(
                rs.getString("loan_id"),
                rs.getInt("term"),
                rs.getBigDecimal("originated_amount"),
                rs.getString("currency"),
                rs.getBigDecimal("target_interest_rate"),
                rs.getBigDecimal("effective_interest_rate"),
                rs.getString("external_reference"),
                rs.getDate("start_date").toInstant().atZone(ZoneId.of(rs.getString("timezone"))),
                rs.getDate("end_date").toInstant().atZone(ZoneId.of(rs.getString("timezone"))),
                rs.getString("status"), // CREATED, IN_PROGRESS, CLOSED
                rs.getString("timezone"),
                rs.getString("region"),
                rs.getString("state")
        );
    }
}
