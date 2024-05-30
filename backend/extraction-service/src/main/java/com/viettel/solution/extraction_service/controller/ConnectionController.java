package com.viettel.solution.extraction_service.controller;

import com.viettel.solution.extraction_service.database.DatabaseConnection;
import com.viettel.solution.extraction_service.entity.DatabaseConfig;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class ConnectionController {
    @PostMapping("/connect")
    public ResponseEntity<?> getConnection(@RequestBody DatabaseConfig databaseConfigEntity) {
        try {
            boolean result = DatabaseConnection.createSessionFactory(databaseConfigEntity.getUsernameId(), databaseConfigEntity);
            if (result) {
                return ResponseEntity.ok("Connection successful");
            } else {
                return ResponseEntity.badRequest().body("Connection failed");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Connection failed");
        }
    }
    @GetMapping("/test")
    public ResponseEntity<?> test() {
        //fake json
        DatabaseConfig databaseConfigEntity = new DatabaseConfig();
        databaseConfigEntity.setUrl("jdbc:mysql://localhost:3306/12");
        databaseConfigEntity.setUsername("root");
        databaseConfigEntity.setPassword("root");
        databaseConfigEntity.setType("mysql");
        return ResponseEntity.ok(databaseConfigEntity);

    }

    @PostMapping("/disconnect")
    public ResponseEntity<?> disconnect(@RequestBody DatabaseConfig databaseConfigEntity) {
        try {

            boolean result = DatabaseConnection.closeSessionFactory("12", "mysql");

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
