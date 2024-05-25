package com.viettel.solution.extraction_service.controller;


import com.viettel.solution.extraction_service.entity.Column;
import com.viettel.solution.extraction_service.entity.DatabaseConfig;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/column")
public class ColumnController {
    @PostMapping("/column")
    public ResponseEntity<Column> getTable(@RequestBody DatabaseConfig databaseConfigEntity, String tableName, String columnName) {
    /*    Column columnEntity = extractionService.getTableStructure(databaseConfigEntity, tableName, columnName);
        System.out.println(columnEntity.toString());
        return ResponseEntity.ok(columnEntity);*/

        return null;
    }
}
