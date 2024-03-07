package org.corefin.model.common;

public enum InstallmentStatus {
    OWED,
    PAID,
    EARLY,
    LATE, // late but paid
    OVERDUE, // late, but unpaid
    REFUNDED,
    CANCELED
}
