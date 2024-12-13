package org.cinema.util;

import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;

/**
 * Utility class for password hashing and validation using bcrypt.
 * This class provides methods to securely hash passwords with a salt and verify passwords against stored hashes.
 */
@Slf4j
public class PasswordUtil {

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

    public static boolean checkPassword(String password, String storedHash) {
        try {
            return BCrypt.checkpw(password, storedHash);
        } catch (Exception e) {
            log.error("Error during password checking: {}", e.getMessage());
            throw new RuntimeException("Error checking password", e);
        }
    }
}
