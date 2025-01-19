package org.cinema.exception;

/**
 * Exception thrown when an entity already exists in the system.
 */
public class EntityAlreadyExistException extends RuntimeException {
    public EntityAlreadyExistException(String message) {
        super(message);
    }
}
