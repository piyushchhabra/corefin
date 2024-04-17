package org.corefin.dao;

import org.corefin.dao.mappers.PaymentMapper;
import org.corefin.dto.PaymentDto;
import org.jdbi.v3.core.Jdbi;

import java.util.List;

public class PaymentDao implements BaseDao<PaymentDto> {
    private Jdbi jdbi;
    public PaymentDao(Jdbi jdbi) {
        this.jdbi = jdbi;
        registerRowMapper();
    }

    @Override
    public void insert(PaymentDto dto) {
        String insertQuery = """
                INSERT INTO payment(
                                  loan_id,
                                  amount,
                                  payment_type,
                                  payment_datetime)
                VALUES (
                :loan_id,
                :amount,
                :payment_type,
                :payment_datetime)
                """;
        // TODO: check if row count == 0 -> that means an exception happened
        jdbi.useHandle(
                handle -> {
                    handle.createUpdate(insertQuery)
                            .bind("loan_id", dto.loanId())
                            .bind("amount", dto.amount())
                            .bind("payment_type", dto.paymentType().toString())
                            .bind("payment_datetime", dto.paymentDateTime())
                            .execute();
                }
        );

    }

    @Override
    public PaymentDto findById(String paymentId) {
        return jdbi.withHandle(
                handle -> handle.createQuery("SELECT * FROM payment WHERE payment_id= :payment_id")
                        .bind("payment_id", paymentId)
                        .mapTo(PaymentDto.class)
                        .findOne()
                        .orElse(null)
        );
    }

    @Override
    public void registerRowMapper() {
        jdbi.registerRowMapper(new PaymentMapper());
    }

    public List<PaymentDto> findByLoanId(String loanId) {
        return jdbi.withHandle(
                handle ->
                    handle.createQuery("SELECT * FROM payment WHERE loan_id= :loan_id")
                            .bind("loan_id", loanId)
                            .mapTo(PaymentDto.class)
                            .list()
        );
    }

    public List<PaymentDto> findAll() {
        return jdbi.withHandle(
                handle ->
                        handle.createQuery("SELECT * FROM payment")
                                .mapTo(PaymentDto.class)
                                .list()
        );
    }
}
