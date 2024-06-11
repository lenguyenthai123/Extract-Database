package com.viettel.solution.extraction_service.controller;

import com.viettel.solution.extraction_service.database.DatabaseConnection;
import com.viettel.solution.extraction_service.entity.DatabaseConfig;
import com.viettel.solution.extraction_service.service.ConnectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class ConnectionController {

    @Autowired
    private ConnectionService connectionService;

    @PostMapping("/connect")
    public ResponseEntity<?> getConnection(@RequestBody DatabaseConfig databaseConfigEntity) {
        try {
            boolean result = connectionService.connect(databaseConfigEntity.getUsernameId(), databaseConfigEntity);
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

    @PostMapping("/disconnect")
    public ResponseEntity<?> disconnect(@RequestBody DatabaseConfig databaseConfigEntity) {
        try {

            boolean result = connectionService.disconnect(databaseConfigEntity.getUsernameId(), databaseConfigEntity.getType());

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
