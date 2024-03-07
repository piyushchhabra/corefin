package org.corefin.dao.mappers;


import org.corefin.dto.LoanInstallmentDto;
import org.corefin.model.common.InstallmentStatus;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;

// Converts the raw database object ResultSet into the LoanDto object
public class LoanInstallmentMapper implements RowMapper<LoanInstallmentDto> {

    @Override
    public LoanInstallmentDto map(ResultSet rs, StatementContext ctx) throws SQLException {
        return new LoanInstallmentDto(
                rs.getString("loan_installment_id"),
                InstallmentStatus.valueOf(rs.getString("status")),
                rs.getString("loan_id"),
                rs.getInt("num_term"),
                rs.getBigDecimal("principal_amount"),
                rs.getBigDecimal("interest_amount"),
                rs.getDate("start_date").toLocalDate(),
                rs.getDate("due_date").toLocalDate(),
                rs.getDate("end_date").toLocalDate()
        );
    }
}
