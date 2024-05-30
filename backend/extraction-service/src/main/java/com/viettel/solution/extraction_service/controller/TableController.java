package com.viettel.solution.extraction_service.controller;

import com.viettel.solution.extraction_service.dto.RequestDto;
import com.viettel.solution.extraction_service.entity.DatabaseConfig;
import com.viettel.solution.extraction_service.entity.Table;
import com.viettel.solution.extraction_service.service.TableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/table")
public class TableController {


    @Autowired
    private TableService tableService;

    @GetMapping
    public ResponseEntity<Table> getTable(@ModelAttribute RequestDto req) {
        System.out.println("Table name" + req.getTable());
        Table table = tableService.getTableStructure(req);
        if (table == null) {
            return ResponseEntity.badRequest().body(null);
        } else {
            return ResponseEntity.ok(table);
        }

    }
}
