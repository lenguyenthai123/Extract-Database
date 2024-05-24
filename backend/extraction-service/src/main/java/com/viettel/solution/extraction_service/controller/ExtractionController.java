package com.viettel.solution.extraction_service.controller;


import com.viettel.solution.extraction_service.database.DatabaseConnection;
import com.viettel.solution.extraction_service.entity.Column;
import com.viettel.solution.extraction_service.entity.DatabaseConfig;
import com.viettel.solution.extraction_service.entity.DatabaseStructure;
import com.viettel.solution.extraction_service.entity.Table;
import com.viettel.solution.extraction_service.service.ExtractionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Connection;

@RestController
@RequestMapping("/extraction")
public class ExtractionController {

    @Autowired
    private ExtractionService extractionService;

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

    @PostMapping("/database")
    public ResponseEntity<DatabaseStructure> getDatabase(@RequestBody DatabaseConfig databaseConfigEntity) {
        DatabaseStructure databaseEntity = extractionService.getDatabaseStructure(databaseConfigEntity);
        System.out.println(databaseEntity.toString());
        return ResponseEntity.ok(databaseEntity);
    }

    @PostMapping("/table")
    public ResponseEntity<Table> getTable(@RequestBody DatabaseConfig databaseConfigEntity, String tableName) {
        System.out.println("Table name" + tableName);
        Table tableEntity = extractionService.getTableStructure(databaseConfigEntity, tableName);

        return ResponseEntity.ok(tableEntity);
    }

    @PostMapping("/column")
    public ResponseEntity<Column> getTable(@RequestBody DatabaseConfig databaseConfigEntity, String tableName, String columnName) {
        Column columnEntity = extractionService.getTableStructure(databaseConfigEntity, tableName, columnName);
        System.out.println(columnEntity.toString());
        return ResponseEntity.ok(columnEntity);
    }
}
