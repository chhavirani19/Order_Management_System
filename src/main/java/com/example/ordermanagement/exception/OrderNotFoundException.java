package com.example.ordermanagement.exception;

/** Order ID is not in the table -> 404 Not Found in the REST API. */
public class OrderNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public OrderNotFoundException(int orderId) {
        super("Order with ID " + orderId + " does not exist.");
    }
}
