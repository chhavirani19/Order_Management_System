package com.example.ordermanagement.exception;

/**
 * Wrong input or a rule is broken (invalid status, quantity <= 0,
 * deleting an order that is not Cancelled, ...) -> 400 Bad Request.
 */
public class InvalidOrderException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public InvalidOrderException(String message) {
        super(message);
    }
}
