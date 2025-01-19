package org.cinema.exception;

/**
 * Exception thrown when no data is found for a given query.
 */
public class NoDataFoundException extends RuntimeException {
    public NoDataFoundException(String message) {
        super(message);
    }
}
