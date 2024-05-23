package com.viettel.solution.extraction_service.controller;


import com.viettel.solution.extraction_service.entity.DatabaseConfigEntity;
import com.viettel.solution.extraction_service.entity.DatabaseEntity;
import com.viettel.solution.extraction_service.service.ExtractionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/extraction")
public class ExtractionController {


    @Autowired
    private ExtractionService extractionService;

    @PostMapping("/database")
    public ResponseEntity<DatabaseEntity> getDatabase(@RequestBody DatabaseConfigEntity databaseConfigEntity) {
        DatabaseEntity databaseEntity = extractionService.getDatabaseStructure(databaseConfigEntity);
        System.out.println(databaseEntity.toString());
        return ResponseEntity.ok(databaseEntity);
    }
}
