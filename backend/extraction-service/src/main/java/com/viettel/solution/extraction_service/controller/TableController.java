package com.viettel.solution.extraction_service.controller;

import com.viettel.solution.extraction_service.dto.RequestDto;
import com.viettel.solution.extraction_service.entity.DatabaseConfig;
import com.viettel.solution.extraction_service.entity.Table;
import com.viettel.solution.extraction_service.service.TableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/table")
public class TableController {


    @Autowired
    private TableService tableService;

    @GetMapping("/list")
    public ResponseEntity<List<Table>> getTable(@ModelAttribute RequestDto req) {
        List<Table> tables = tableService.getAllName(req);
        if (tables == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(tables);
        }
    }
}
