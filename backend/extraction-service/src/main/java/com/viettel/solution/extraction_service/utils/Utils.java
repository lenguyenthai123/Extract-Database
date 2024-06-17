package com.viettel.solution.extraction_service.utils;

import java.io.*;
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

    public static void saveFile(ByteArrayOutputStream outputStream, String outputFilePath) throws IOException {

        // Tạo đối tượng File cho tệp đích
        File file = new File(outputFilePath);

        // Viết nội dung ByteArrayOutputStream vào tệp đích
        try (FileOutputStream fos = new FileOutputStream(file)) {
            outputStream.writeTo(fos);
            System.out.println("File saved successfully at " + outputFilePath);
        }
    }

    public static void deleteFile(String filePath) {
        // Tạo đối tượng File cho tệp cần xóa
        File file = new File(filePath);

        // Xóa tệp nếu tồn tại
        if (file.exists()) {
            file.delete();
            System.out.println("File deleted successfully");
        } else {
            System.out.println("File not found");
        }
    }
}
