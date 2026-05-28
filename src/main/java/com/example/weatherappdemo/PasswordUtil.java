package com.example.weatherappdemo;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class PasswordUtil {

    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int ITERATIONS = 120_000;
    private static final int SALT_BYTES = 16;
    private static final int HASH_BITS = 256;

    // REGISTER sırasında kullanılır
    public static String hashPassword(String password) {

        byte[] salt = createSalt();

        byte[] hash = pbkdf2(password, salt, ITERATIONS, HASH_BITS);

        return ALGORITHM + ":" +
                ITERATIONS + ":" +
                Base64.getEncoder().encodeToString(salt) + ":" +
                Base64.getEncoder().encodeToString(hash);
    }

    // LOGIN sırasında kullanılır
    public static boolean verifyPassword(String password, String storedHash) {

        if (password == null || storedHash == null || storedHash.isBlank()) {
            return false;
        }

        String[] parts = storedHash.split(":");

        if (parts.length != 4) {
            return false;
        }

        try {

            int iterations = Integer.parseInt(parts[1]);

            byte[] salt = Base64.getDecoder().decode(parts[2]);

            byte[] expectedHash = Base64.getDecoder().decode(parts[3]);

            byte[] actualHash = pbkdf2(
                    password,
                    salt,
                    iterations,
                    expectedHash.length * 8
            );

            return MessageDigest.isEqual(expectedHash, actualHash);

        } catch (Exception e) {
            return false;
        }
    }

    private static byte[] createSalt() {

        byte[] salt = new byte[SALT_BYTES];

        new SecureRandom().nextBytes(salt);

        return salt;
    }

    private static byte[] pbkdf2(
            String password,
            byte[] salt,
            int iterations,
            int hashBits
    ) {

        try {

            PBEKeySpec spec = new PBEKeySpec(
                    password.toCharArray(),
                    salt,
                    iterations,
                    hashBits
            );

            SecretKeyFactory factory =
                    SecretKeyFactory.getInstance(ALGORITHM);

            return factory.generateSecret(spec).getEncoded();

        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {

            throw new IllegalStateException(
                    "Password hashing is not available.",
                    e
            );
        }
    }
}