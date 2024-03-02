package org.corefin.calculator.model;

public enum InstallmentStatus {
    OWED,
    PAID,
    EARLY,
    LATE, // late but paid
    OVERDUE, // late, but unpaid
    REFUNDED,
    CANCELED
}
