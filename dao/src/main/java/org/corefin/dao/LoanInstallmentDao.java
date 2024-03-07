package org.corefin.dao;

import org.corefin.dao.mappers.LoanInstallmentMapper;
import org.corefin.dto.LoanInstallmentDto;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;

public class LoanInstallmentDao implements BaseDao<LoanInstallmentDto> {
    private Jdbi jdbi;
    public LoanInstallmentDao(Jdbi jdbi) {
        this.jdbi = jdbi;
        registerRowMapper();
    }
    // TODO: Pass in Handle here
    public void insert(LoanInstallmentDto dto) {
        jdbi.useHandle(
                handle -> insert(dto, handle)
        );
    }
    public void insert(LoanInstallmentDto dto, Handle handle) {
        String insertQuery = """
                INSERT INTO loan_installment (
                                  loan_id,
                                  num_term,
                                  principal_amount,
                                  interest_amount,
                                  due_date,
                                  status,
                                  start_date,
                                  end_date)
                VALUES (
                :loan_id,
                :num_term,
                :principal_amount,
                :interest_amount,
                :due_date,
                :status,
                :start_date,
                :end_date)
                """;
        // TODO: check if row count == 0 -> that means an exception happened
        handle.createUpdate(insertQuery)
                .bind("loan_id", dto.loanId())
                .bind("num_term", dto.numTerm())
                .bind("principal_amount", dto.principalAmount())
                .bind("interest_amount", dto.interestAmount())
                .bind("due_date", dto.dueDate())
                .bind("status", dto.status().toString())
                .bind("start_date", dto.startDate())
                .bind("end_date", dto.dueDate())
                .execute();
    }

    @Override
    public LoanInstallmentDto findById(String loanInstallmentId) {
        return jdbi.withHandle(
                handle -> handle.createQuery("SELECT * FROM loan_installment WHERE loan_installment_id = :loan_installment_id")
                        .bind("loan_installment_id", loanInstallmentId)
                        .mapTo(LoanInstallmentDto.class)
                        .findOne()
                        .orElse(null)
        );
    }
    @Override
    public void registerRowMapper() {
        jdbi.registerRowMapper(new LoanInstallmentMapper());
    }
}
