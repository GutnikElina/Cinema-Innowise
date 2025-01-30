package org.cinema.util;

import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;

/**
 * Utility class for password hashing using bcrypt.
 * Provides methods to hash passwords.
 */
@Slf4j
public class PasswordUtil {

    /**
     * Hashes the given password using bcrypt.
     *
     * @param password the password to hash
     * @return the hashed password
     */
    public static String hashPassword(String password) {
        try {
            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
            log.debug("Password hashed successfully.");
            return hashedPassword;
        } catch (Exception e) {
            log.error("Unexpected error during password hashing: {}", e.getMessage());
            throw new RuntimeException("Unexpected error during hashing password", e);
        }
    }
}