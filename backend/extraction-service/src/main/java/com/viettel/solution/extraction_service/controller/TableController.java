package com.viettel.solution.extraction_service.controller;

import com.viettel.solution.extraction_service.dto.RequestDto;
import com.viettel.solution.extraction_service.entity.DatabaseConfig;
import com.viettel.solution.extraction_service.entity.Table;
import com.viettel.solution.extraction_service.service.TableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/table")
public class TableController {


    @Autowired
    private TableService tableService;

    @PostMapping("/")
    public ResponseEntity<Table> getTable(@RequestBody RequestDto req) {
        System.out.println("Table name" + req.getTable());
/*        Table tableEntity = tableService.getTableStructure(req.getUsernameId(), req.getType(), req.getDatabaseName(), req.getTable());

        return ResponseEntity.ok(tableEntity);*/
        return null;
    }
}
