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
    public static String hashPassword(String password) {
        try {
            log.debug("Starting password hashing...");
            byte[] salt = generateSalt();
            log.debug("Generated salt: {}", Base64.getEncoder().encodeToString(salt));
            byte[] hash = sha256(password, salt);
            log.debug("Password hashed successfully.");
            return Base64.getEncoder().encodeToString(salt) + ":" + Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            log.error("Error during password hashing: {}", e.getMessage(), e);
            throw new RuntimeException("Error hashing password", e);
        }
    }

    /**
     * Проверяет, соответствует ли введённый пароль хэшу.
     *
     * @param password введённый пароль.
     * @param storedHash хэш пароля, сохранённый в базе данных.
     * @return true, если пароль совпадает.
     */
    public static boolean verifyPassword(String password, String storedHash) {
        try {
            log.debug("Starting password verification...");
            String[] parts = storedHash.split(":");
            if (parts.length != 2) {
                log.warn("Stored hash format is invalid.");
                throw new IllegalArgumentException("Invalid stored hash format");
            }
            log.debug("Extracted salt and hash from stored hash...");
            byte[] salt = Base64.getDecoder().decode(parts[0]);
            byte[] storedHashBytes = Base64.getDecoder().decode(parts[1]);
            byte[] computedHash = sha256(password, salt);
            log.debug("Computed hash for verification...");
            boolean isEqual = MessageDigest.isEqual(storedHashBytes, computedHash);
            if (isEqual) {
                log.info("Password verification successful!");
            } else {
                log.warn("Password verification failed!");
            }
            return isEqual;
        } catch (NoSuchAlgorithmException e) {
            log.error("Error during password verification: {}", e.getMessage(), e);
            throw new RuntimeException("Error verifying password", e);
        }
    }

    /**
     * Генерирует случайную соль
     *
     * @return массив байтов соли
     */
    private static byte[] generateSalt() {
        try {
            log.debug("Generating random salt...");
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[SALT_LENGTH];
            random.nextBytes(salt);
            log.debug("Salt generated successfully!");
            return salt;
        } catch (Exception e) {
            log.error("Error generating salt: {}", e.getMessage(), e);
            throw new RuntimeException("Error generating salt", e);
        }
    }

    /**
     * Хэширует пароль с использованием SHA-256.
     *
     * @param password пароль в виде строки.
     * @param salt соль.
     * @return массив байтов хэша.
     */
    private static byte[] sha256(String password, byte[] salt) throws NoSuchAlgorithmException {
        try {
            log.debug("Hashing password with SHA-256...");
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(salt);
            log.debug("SHA-256 hash generated successfully!");
            return digest.digest(password.getBytes());
        } catch (NoSuchAlgorithmException e) {
            log.error("Error during SHA-256 hashing: {}", e.getMessage(), e);
            throw e;
        }
    }
}
