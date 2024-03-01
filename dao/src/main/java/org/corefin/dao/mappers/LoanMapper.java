package org.corefin.dao.mappers;


import org.corefin.dto.LoanDto;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;

// Converts the raw database object ResultSet into the LoanDto object
public class LoanMapper implements RowMapper<LoanDto> {

    @Override
    public LoanDto map(ResultSet rs, StatementContext ctx) throws SQLException {
        // TODO: implement this
        return null;
    }
}
