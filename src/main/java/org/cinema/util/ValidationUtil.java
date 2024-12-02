package org.cinema.util;

import org.cinema.model.Role;

public class ValidationUtil {

    public static void validateUsername(String username) {
        if (username == null || username.length() < 5 || !Character.isLetter(username.charAt(0))) {
            throw new IllegalArgumentException("Username must be at least 5 characters long and start with a letter.");
        }
    }

    public static void validatePassword(String password) {
        if (password == null || password.length() < 5) {
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
}
