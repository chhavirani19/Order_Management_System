package com.example.ordermanagement.exception;

/** Order ID already exists (primary key) -> 409 Conflict in the REST API. */
public class DuplicateOrderException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DuplicateOrderException(int orderId) {
        super("Order ID " + orderId + " already exists.");
    }
}
