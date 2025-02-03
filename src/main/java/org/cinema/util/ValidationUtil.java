package org.cinema.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Slf4j
public class ValidationUtil {

    public static void validateIsPositive(int id) {
        if (id <= 0) {
            log.error("Validation failed: ID '{}' is not positive", id);
            throw new IllegalArgumentException("ID must be a positive integer.");
        }
    }

    public static void validateUsername(String username) {
        validateNotBlank(username, "Username");
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
        validateNotBlank(password, "Password");
        if (password.length() < 5) {
            log.error("Validation failed: password is too short");
            throw new IllegalArgumentException("Password must be at least 5 characters long.");
        }
    }

    public static void validateParameters(String action, String ticketIdParam) {
        validateNotBlank(action, "Action");
        validateNotBlank(ticketIdParam, "Ticket ID");
        parseId(ticketIdParam);
    }

    public static void validateDate(LocalDate date) {
        validateNotBlank(String.valueOf(date), "Date");
        try {
            if (date.isBefore(LocalDate.now())) {
                log.error("Validation failed: date '{}' is in the past", date);
                throw new IllegalArgumentException("Date cannot be in the past.");
            }
        } catch (Exception e) {
            log.error("Validation failed: date '{}' has invalid format or value", date);
            throw new IllegalArgumentException("Invalid date format or value.");
        }
    }

    public static void validatePrice(BigDecimal price) {
        validateNotBlank(String.valueOf(price), "Price");
        try {
            if (price.compareTo(BigDecimal.ZERO) <= 0) {
                log.error("Validation failed: price '{}' is not positive", price);
                throw new IllegalArgumentException("Price must be a positive value.");
            }
        } catch (NumberFormatException e) {
            log.error("Validation failed: price '{}' has invalid format", price);
            throw new IllegalArgumentException("Invalid price format.");
        }
    }

    public static void validateCapacity(Integer capacity) {
        validateNotBlank(String.valueOf(capacity), "Capacity");
        try {
            if (capacity <= 0) {
                log.error("Validation failed: capacity '{}' is not positive", capacity);
                throw new IllegalArgumentException("Capacity must be a positive number.");
            }
        } catch (NumberFormatException e) {
            log.error("Validation failed: capacity '{}' has invalid format", capacity);
            throw new IllegalArgumentException("Invalid capacity format.");
        }
    }

    public static void validateSeatNumber(String seatNumberStr, int capacity) {
        validateNotBlank(seatNumberStr, "Seat number");
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

    public static long parseLong(String id) {
        validateNotBlank(id, "ID");
        try {
            long parsedId = Long.parseLong(id);
            validateIsPositive((int) parsedId);
            return parsedId;
        } catch (NumberFormatException e) {
            log.error("Validation failed: long ID '{}' has invalid format", id);
            throw new IllegalArgumentException("ID must be a valid positive integer.");
        }
    }

    public static void parseId(String id) {
        validateNotBlank(id, "ID");
        try {
            int parsedId = Integer.parseInt(id);
            validateIsPositive(parsedId);
        } catch (NumberFormatException e) {
            log.error("Validation failed: int ID '{}' has invalid format", id);
            throw new IllegalArgumentException("ID must be a valid positive integer.");
        }
    }

    public static void validateTime(LocalTime startTime, LocalTime endTime) {
        validateNotBlank(String.valueOf(startTime), "Start time");
        validateNotBlank(String.valueOf(endTime), "End time");

        if (startTime.isAfter(endTime) || startTime.equals(endTime)) {
            log.error("Validation failed: start time '{}' is not before end time '{}'", startTime, endTime);
            throw new IllegalArgumentException("Start time must be before end time.");
        }
    }

    public static void validateNotBlank(String value, String fieldName) {
        if (StringUtils.isBlank(value)) {
            log.error("Validation failed: {} is null or empty", fieldName);
            throw new IllegalArgumentException(fieldName + " cannot be null or empty.");
        }
    }

    public static void validateParameters(BigDecimal price, LocalDate date, Integer capacity,
                                          LocalTime start, LocalTime end) {
        validatePrice(price);
        validateDate(date);
        validateCapacity(capacity);
        validateTime(start, end);
    }
}
