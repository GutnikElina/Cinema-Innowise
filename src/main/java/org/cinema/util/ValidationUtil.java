package org.cinema.util;

import lombok.extern.slf4j.Slf4j;
import org.cinema.model.Role;

import java.math.BigDecimal;
import java.time.LocalDate;

@Slf4j
public class ValidationUtil {

    public static void validateIsPositive(int id) {
        if (id <= 0) {
            log.error("Validation failed: ID '{}' is not positive", id);
            throw new IllegalArgumentException("ID must be a positive integer.");
        }
    }

    public static void validateMovieTitle(String title) {
        if (isNullOrBlank(title)) {
            log.error("Validation failed: movie title is null or empty");
            throw new IllegalArgumentException("Movie title must not be null or empty.");
        }
    }

    public static void validateUsername(String username) {
        if (isNullOrBlank(username)) {
            log.error("Validation failed: username is null or empty");
            throw new IllegalArgumentException("Username cannot be null or empty.");
        }
        if (username.length() < 5) {
            log.error("Validation failed: username '{}' is too short", username);
            throw new IllegalArgumentException("Username must be at least 5 characters long.");
        }
        if (!Character.isLetter(username.charAt(0))) {
            log.error("Validation failed: username '{}' does not start with a letter", username);
            throw new IllegalArgumentException("Username must start with a letter.");
        }
    }

    public static void validatePassword(String password) {
        if (isNullOrBlank(password)) {
            log.error("Validation failed: password is null or empty");
            throw new IllegalArgumentException("Password cannot be null or empty.");
        }
        if (password.length() < 5) {
            log.error("Validation failed: password is too short");
            throw new IllegalArgumentException("Password must be at least 5 characters long.");
        }
    }

    public static void validateRole(String role) {
        if (isNullOrBlank(role)) {
            log.error("Validation failed: role is null or empty");
            throw new IllegalArgumentException("Role must be selected.");
        }
        try {
            Role.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.error("Validation failed: role '{}' is not valid", role);
            throw new IllegalArgumentException("Invalid role selected.");
        }
    }

    public static void validateParameters(String action, String ticketIdParam) {
        if (isNullOrBlank(action) || isNullOrBlank(ticketIdParam)) {
            log.error("Validation failed: action or ticket ID is missing");
            throw new IllegalArgumentException("Action or ticket ID is missing!");
        }
        parseId(ticketIdParam);
    }

    public static void validateDate(String dateStr) {
        if (isNullOrBlank(dateStr)) {
            log.error("Validation failed: date is null or empty");
            throw new IllegalArgumentException("Date cannot be null or empty.");
        }
        try {
            LocalDate date = LocalDate.parse(dateStr);
            if (date.isBefore(LocalDate.now())) {
                log.error("Validation failed: date '{}' is in the past", dateStr);
                throw new IllegalArgumentException("Date cannot be in the past.");
            }
        } catch (Exception e) {
            log.error("Validation failed: date '{}' has invalid format or value", dateStr);
            throw new IllegalArgumentException("Invalid date format or value.");
        }
    }

    public static void validatePrice(String priceStr) {
        if (isNullOrBlank(priceStr)) {
            log.error("Validation failed: price is null or empty");
            throw new IllegalArgumentException("Price cannot be null or empty.");
        }
        try {
            BigDecimal price = new BigDecimal(priceStr);
            if (price.compareTo(BigDecimal.ZERO) <= 0) {
                log.error("Validation failed: price '{}' is not positive", priceStr);
                throw new IllegalArgumentException("Price must be a positive value.");
            }
        } catch (NumberFormatException e) {
            log.error("Validation failed: price '{}' has invalid format", priceStr);
            throw new IllegalArgumentException("Invalid price format.");
        }
    }

    public static void validateCapacity(String capacityStr) {
        if (isNullOrBlank(capacityStr)) {
            log.error("Validation failed: capacity is null or empty");
            throw new IllegalArgumentException("Capacity cannot be null or empty.");
        }
        try {
            int capacity = Integer.parseInt(capacityStr);
            if (capacity <= 0) {
                log.error("Validation failed: capacity '{}' is not positive", capacityStr);
                throw new IllegalArgumentException("Capacity must be a positive number.");
            }
        } catch (NumberFormatException e) {
            log.error("Validation failed: capacity '{}' has invalid format", capacityStr);
            throw new IllegalArgumentException("Invalid capacity format.");
        }
    }

    public static void validateSeatNumber(String seatNumberStr, int capacity) {
        if (isNullOrBlank(seatNumberStr)) {
            log.error("Validation failed: seat number is null or empty");
            throw new IllegalArgumentException("Seat number cannot be null or empty.");
        }
        try {
            int seatNum = Integer.parseInt(seatNumberStr);
            if (seatNum > capacity || seatNum <= 0) {
                log.error("Validation failed: seat number '{}' is invalid for capacity '{}'", seatNum, capacity);
                throw new IllegalArgumentException("Seat number exceeds the session's capacity or is not positive.");
            }
        } catch (NumberFormatException e) {
            log.error("Validation failed: seat number '{}' has invalid format", seatNumberStr);
            throw new IllegalArgumentException("Invalid seat number format.");
        }
    }

    public static int parseId(String id) {
        if (isNullOrBlank(id)) {
            log.error("Validation failed: ID is null or empty");
            throw new IllegalArgumentException("ID cannot be null or empty.");
        }
        try {
            int parsedId = Integer.parseInt(id);
            if (parsedId <= 0) {
                log.error("Validation failed: ID '{}' is not positive", id);
                throw new IllegalArgumentException("ID must be a positive integer.");
            }
            return parsedId;
        } catch (NumberFormatException e) {
            log.error("Validation failed: ID '{}' has invalid format", id);
            throw new IllegalArgumentException("ID must be a valid positive integer.");
        }
    }

    private static boolean isNullOrBlank(String str) {
        return str == null || str.isBlank();
    }
}
