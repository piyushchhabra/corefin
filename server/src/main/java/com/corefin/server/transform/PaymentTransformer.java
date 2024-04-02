package com.corefin.server.transform;

import org.corefin.calculator.model.InstallmentMapping;
import org.corefin.calculator.model.Payment;
import org.corefin.dto.PaymentDto;

import java.util.Collections;
import java.util.List;

public class PaymentTransformer {
    public static Payment transform(PaymentDto paymentDto) {
        return new Payment(
                paymentDto.paymentId(),
                paymentDto.amount(),
                paymentDto.paymentDateTime(),
                paymentDto.paymentType(),
                Collections.emptyList()
        );
    }
}
