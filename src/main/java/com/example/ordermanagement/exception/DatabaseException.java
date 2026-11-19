package com.example.ordermanagement.exception;

/** Wraps an SQLException -> 500 Internal Server Error in the REST API. */
public class DatabaseException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DatabaseException(Throwable cause) {
        super("Database error: " + cause.getMessage(), cause);
    }
}
