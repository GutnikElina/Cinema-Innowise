package org.cinema.util;

import lombok.extern.slf4j.Slf4j;
import org.cinema.model.Role;

import javax.xml.bind.ValidationException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

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

    public static void validateRole(String role) {
        validateNotBlank(role, "Role");
        try {
            Role.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.error("Validation failed: role '{}' is not valid", role);
            throw new IllegalArgumentException("Invalid role selected.");
        }
    }

    public static void validateParameters(String action, String ticketIdParam) {
        validateNotBlank(action, "Action");
        validateNotBlank(ticketIdParam, "Ticket ID");
        parseId(ticketIdParam);
    }

    public static void validateDate(String dateStr) {
        validateNotBlank(dateStr, "Date");
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
        validateNotBlank(priceStr, "Price");
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
        validateNotBlank(capacityStr, "Capacity");
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

    public static int parseId(String id) {
        validateNotBlank(id, "ID");
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

    public static void validateTitle(String title) {
        validateNotBlank(title, "Movie title");
    }

    public static void validateTime(String startTimeStr, String endTimeStr) {
        validateNotBlank(startTimeStr, "Start time");
        validateNotBlank(endTimeStr, "End time");

        LocalTime startTime = LocalTime.parse(startTimeStr);
        LocalTime endTime = LocalTime.parse(endTimeStr);

        if (startTime.isAfter(endTime) || startTime.equals(endTime)) {
            log.error("Validation failed: start time '{}' is not before end time '{}'", startTimeStr, endTimeStr);
            throw new IllegalArgumentException("Start time must be before end time.");
        }
    }

    public static void validateNotBlank(String value, String fieldName) {
        if (isNullOrBlank(value)) {
            log.error("Validation failed: {} is null or empty", fieldName);
            throw new IllegalArgumentException(fieldName + " cannot be null or empty.");
        }
    }

    private static boolean isNullOrBlank(String str) {
        return str == null || str.isBlank();
    }
}
