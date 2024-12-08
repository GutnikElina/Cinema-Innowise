package org.cinema.util;

import lombok.extern.slf4j.Slf4j;
import org.cinema.model.Role;
import java.math.BigDecimal;
import java.time.LocalDate;

@Slf4j
public class ValidationUtil {

    public static void validateIsPositive(int id) {
        if (id < 0) {
            throw new IllegalArgumentException("ID must be a positive integer.");
        }
    }

    public static void validateUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be null or empty.");
        }
        if (username.length() < 5) {
            throw new IllegalArgumentException("Username must be at least 5 characters long.");
        }
        if (!Character.isLetter(username.charAt(0))) {
            throw new IllegalArgumentException("Username must start with a letter.");
        }
    }

    public static void validatePassword(String password) {
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password cannot be null or empty.");
        }
        if (password.length() < 5) {
            throw new IllegalArgumentException("Password must be at least 5 characters long.");
        }
    }

    public static void validateRole(String role) {
        if (role == null || role.isEmpty()) {
            throw new IllegalArgumentException("Role must be selected.");
        }
        try {
            Role.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid role selected.");
        }
    }

    public static void validateParameters(String action, String ticketIdParam) {
        if (action == null || ticketIdParam == null || ticketIdParam.isEmpty()) {
            throw new IllegalArgumentException("Action or Ticket ID is missing!");
        }
        try {
            Integer.parseInt(ticketIdParam);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid Ticket ID format!");
        }
    }

    public static void validateDate(String dateStr) {
        try {
            LocalDate date = LocalDate.parse(dateStr);
            if (date.isBefore(LocalDate.now())) {
                throw new IllegalArgumentException("Date cannot be in the past.");
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid date format or value.");
        }
    }

    public static void validatePrice(String priceStr) {
        try {
            BigDecimal price = new BigDecimal(priceStr);
            if (price.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Price must be a positive value.");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid price format.");
        }
    }

    public static void validateCapacity(String capacityStr) {
        try {
            int capacity = Integer.parseInt(capacityStr);
            if (capacity <= 0) {
                throw new IllegalArgumentException("Capacity must be a positive number.");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid capacity format.");
        }
    }

    public static void validateSeatNumber(String seatNumberStr, int capacity) {
        try {
            int seatNum = Integer.parseInt(seatNumberStr);
            if (seatNum > capacity || seatNum <= 0) {
                log.warn("Invalid seat number.");
                throw new IllegalArgumentException("Your seat number exceeds the session's capacity or not positive number! Try again.");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid seat number format.");
        }
    }

    public static int parseId(String id) {
        try {
            return Integer.parseInt(id);
        } catch (NumberFormatException e) {
            log.warn("Invalid ID format: {}", id);
            throw new IllegalArgumentException("ID must be a valid positive integer.");
        }
    }
}
