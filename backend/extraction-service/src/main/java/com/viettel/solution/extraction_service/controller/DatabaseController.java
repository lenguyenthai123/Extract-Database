package com.viettel.solution.extraction_service.controller;

import com.viettel.solution.extraction_service.database.DatabaseConnection;
import com.viettel.solution.extraction_service.dto.RequestDto;
import com.viettel.solution.extraction_service.entity.DatabaseStructure;
import com.viettel.solution.extraction_service.service.DatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;

@RestController
@RequestMapping("/database")
public class DatabaseController {

    @Autowired
    private DatabaseService databaseService;

    @GetMapping
    public ResponseEntity<DatabaseStructure> getDatabase(@ModelAttribute RequestDto requestDto) {
        try {
            DatabaseStructure databaseEntity = databaseService.getDatabaseStructure(requestDto);
            System.out.println(databaseEntity.toString());
            return ResponseEntity.ok(databaseEntity);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/test")
    public ResponseEntity<String> getTest(@ModelAttribute RequestDto requestDto) {
        try {
            Connection connection = DatabaseConnection.getConnection("12", "mysql");
            DatabaseMetaData metaData = connection.getMetaData();

// Database Metadata
            String databaseName = connection.getCatalog();
            String databaseVersion = metaData.getDatabaseProductVersion();
            String databaseURL = metaData.getURL();
            String databaseUser = metaData.getUserName();

            System.out.println("Database Name: " + databaseName);
            System.out.println("Database Version: " + databaseVersion);
            System.out.println("Database URL: " + databaseURL);
            System.out.println("Database User: " + databaseUser);

            // Schema Metadata
            ResultSet schemas = metaData.getSchemas();
            while (schemas.next()) {
                String schemaName = schemas.getString("TABLE_SCHEM");
                String owner = schemas.getString("TABLE_CATALOG");  // Owner information might not be directly available
                // Assuming the creation date is not directly available via metadata
                // This information might need to be retrieved in a database-specific way

                System.out.println("Schema Name: " + schemaName);
                System.out.println("Owner: " + owner);
                // System.out.println("Creation Date: " + creationDate); // Not available via standard JDBC
            }
            schemas.close();
            return ResponseEntity.ok("oke");

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}
