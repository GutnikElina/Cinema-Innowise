package org.cinema.util;

import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;

/**
 * Utility class for password hashing and validation using bcrypt.
 * This class provides methods to securely hash passwords with a salt and verify passwords against stored hashes.
 */
@Slf4j
public class PasswordUtil {

    /**
     * Generates a hashed password with a salt using bcrypt.
     * The bcrypt algorithm is designed to securely hash passwords and is resistant to brute-force attacks.
     *
     * @param password the password to be hashed.
     * @return a string containing the hashed password with the salt embedded in the bcrypt format.
     * @throws RuntimeException if an error occurs during password hashing.
     */
    public static String hashPassword(String password) {
        try {
            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
            log.debug("Password hashed successfully.");
            return hashedPassword;
        } catch (Exception e) {
            log.error("Unexpected error during password hashing: {}", e.getMessage(), e);
            throw new RuntimeException("Unexpected error hashing password", e);
        }
    }

    /**
     * Verifies whether the provided password matches the stored password hash.
     * This method compares the raw password with the hashed version stored in the database.
     *
     * @param password   the password to check.
     * @param storedHash the stored bcrypt hash of the password.
     * @return {@code true} if the password matches the stored hash, {@code false} otherwise.
     * @throws RuntimeException if an error occurs during password comparison.
     */
    public static boolean checkPassword(String password, String storedHash) {
        try {
            return BCrypt.checkpw(password, storedHash);
        } catch (Exception e) {
            log.error("Error during password checking: {}", e.getMessage(), e);
            throw new RuntimeException("Error checking password", e);
        }
    }
}
