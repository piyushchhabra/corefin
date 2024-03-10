package org.corefin.calculator.model;

import org.corefin.model.common.PaymentType;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

public record Payment(
       BigDecimal amount,
       ZonedDateTime paymentDateTime,
       PaymentType paymentType,
       // Calculator module generates the necessary
       // installment mappings to the payment amounts
       // received from the user or application
       List<InstallmentMapping> installmentMappings
) {
}
