package com.viettel.solution.extraction_service.controller;

import com.viettel.solution.extraction_service.dto.RequestDto;
import com.viettel.solution.extraction_service.entity.DatabaseStructure;
import com.viettel.solution.extraction_service.service.DatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/database")
public class DatabaseController {

    @Autowired
    private DatabaseService databaseService;

    @GetMapping("")
    public ResponseEntity<DatabaseStructure> getDatabase(@ModelAttribute RequestDto requestDto) {
        try {
            DatabaseStructure databaseEntity = databaseService.getDatabaseStructure(requestDto);
            System.out.println(databaseEntity.toString());
            return ResponseEntity.ok(databaseEntity);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}
