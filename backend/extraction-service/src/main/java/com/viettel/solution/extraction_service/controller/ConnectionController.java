package com.viettel.solution.extraction_service.controller;

import com.viettel.solution.extraction_service.database.DatabaseConnection;
import com.viettel.solution.extraction_service.entity.DatabaseConfig;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class ConnectionController {
    @PostMapping("/connect")
    public ResponseEntity<?> getConnection(@RequestBody DatabaseConfig databaseConfigEntity) {
        try {
            boolean result = DatabaseConnection.createConnection("12", databaseConfigEntity);
            if (result) {
                return ResponseEntity.ok("Connection successful");
            } else {
                return ResponseEntity.badRequest().body("Connection failed");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Connection failed");
        }
    }

    @PostMapping("/disconnect")
    public ResponseEntity<?> disconnect(@RequestBody DatabaseConfig databaseConfigEntity) {
        try {

            boolean result = DatabaseConnection.closeConnection("12", "mysql");

            if (result) {
                return ResponseEntity.ok("Connection closed");
            } else {
                return ResponseEntity.badRequest().body("Connection failed to close");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Connection failed to close");
        }
    }
}
