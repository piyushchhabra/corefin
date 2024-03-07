package org.corefin.dao.mappers;

import org.corefin.dto.PaymentDto;
import org.corefin.model.common.PaymentType;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneId;

public class PaymentMapper implements RowMapper<PaymentDto> {
    @Override
    public PaymentDto map(ResultSet rs, StatementContext ctx) throws SQLException {
        return new PaymentDto(
                rs.getString("payment_id"),
                rs.getString("loan_id"),
                rs.getBigDecimal("amount"),
                PaymentType.valueOf(rs.getString("payment_type")),
                rs.getTimestamp("payment_datetime").toInstant().atZone(ZoneId.of("UTC"))
        );
    }
}
