package org.cinema.util;

import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;

/**
 * Utility class for password hashing and validation using bcrypt.
 * Provides methods to hash passwords and verify them against stored hashes.
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

    /**
     * Verifies if the given password matches the stored hash.
     *
     * @param password the password to check
     * @param storedHash the stored password hash
     * @return true if the password matches the hash, false otherwise
     */
    public static boolean checkPassword(String password, String storedHash) {
        try {
            return BCrypt.checkpw(password, storedHash);
        } catch (Exception e) {
            log.error("Error during password checking: {}", e.getMessage());
            throw new RuntimeException("Error checking password", e);
        }
    }
}