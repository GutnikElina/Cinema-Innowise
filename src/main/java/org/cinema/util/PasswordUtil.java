package org.cinema.util;

import lombok.extern.slf4j.Slf4j;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

@Slf4j
public class PasswordUtil {

    private static final int SALT_LENGTH = 16;

    /**
     * Генерирует хэш пароля с солью.
     *
     * @param password пароль в виде строки.
     * @return строка, содержащая соль и хэш, разделённые двоеточием.
     */
    public static String hashPassword(String password, String salt) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(Base64.getDecoder().decode(salt));
            byte[] hash = digest.digest(password.getBytes());
            log.debug("Password hashed successfully.");
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            log.error("Error during password hashing: {}", e.getMessage());
            throw new RuntimeException("Error hashing password", e);
        } catch (Exception e) {
            log.error("Unexpected error during password hashing: {}", e.getMessage());
            throw new RuntimeException("Unexpected error hashing password", e);
        }
    }

    public static String generateSalt() {
        try {
            log.debug("Generating random salt...");
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[SALT_LENGTH];
            random.nextBytes(salt);
            log.debug("Salt generated successfully!");
            return Base64.getEncoder().encodeToString(salt);
        } catch (Exception e) {
            log.error("Error generating salt: {}", e.getMessage());
            throw new RuntimeException("Error generating salt", e);
        }
    }
}
