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
            log.error("Error during password hashing: {}", e.getMessage());
            throw new RuntimeException("Error hashing password", e);
        } catch (Exception e) {
            log.error("Unexpected error during password hashing: {}", e.getMessage());
            throw new RuntimeException("Unexpected error hashing password", e);
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
            log.error("Error generating salt: {}", e.getMessage());
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
            log.error("SHA-256 algorithm not available: {}", e.getMessage());
            throw new RuntimeException("Error hashing password with SHA-256", e);
        }
    }
}
