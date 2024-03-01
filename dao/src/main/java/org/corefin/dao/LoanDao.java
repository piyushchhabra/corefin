package org.corefin.dao;

import org.corefin.dao.mappers.LoanMapper;
import org.corefin.dto.LoanDto;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.RowMapper;

// TODO: implement me
public class LoanDao implements BaseDao<LoanDto> {
    private Jdbi jdbi;
    public LoanDao(Jdbi jdbi) {
        this.jdbi = jdbi;
    }
    // TODO: Pass in Handle here
    @Override
    public LoanDto insert(LoanDto dto) {
        return null;
    }

    @Override
    public LoanDto findById(String id) {
        return null;
    }
    @Override
    public void registerRowMapper() {
        this.jdbi.registerRowMapper(new LoanMapper());
    }
}
