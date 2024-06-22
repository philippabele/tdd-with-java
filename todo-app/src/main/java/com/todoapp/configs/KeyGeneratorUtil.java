package com.todoapp.configs;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class KeyGeneratorUtil {

    public static String generateSecretKey() {
        try {
            // Create a secure random number generator
            SecureRandom secureRandom = SecureRandom.getInstanceStrong();
            // Create byte array to hold the key
            byte[] key = new byte[32]; // 256 bits
            // Fill the byte array with random bytes
            secureRandom.nextBytes(key);
            // Return the base64 encoded string
            return Base64.getUrlEncoder().withoutPadding().encodeToString(key);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("No secure random algorithm available", e);
        }
    }

    public static void main(String[] args) {
        String secretKey = generateSecretKey();
        System.out.println("Generated Secret Key: " + secretKey);
    }
}
