package com.viettel.solution.extraction_service.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.UUID;

public class Utils {
    public static String hashString(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(input.getBytes());

            // Encode the hash value to a base64 string
            String hashedValue = Base64.getEncoder().encodeToString(hashBytes);

            return hashedValue;
        } catch (NoSuchAlgorithmException e) {
            // Handle the exception appropriately
            e.printStackTrace();
            return null;
        }
    }

    public static String generateUUID() {
        // Tạo UUID ngẫu nhiên
        UUID uuid = UUID.randomUUID();
        // Chuyển UUID thành chuỗi và trả về
        return uuid.toString();
    }
}
