package com.viettel.solution.extraction_service.controller;


import com.viettel.solution.extraction_service.dto.ColumnDto;
import com.viettel.solution.extraction_service.dto.RequestDto;
import com.viettel.solution.extraction_service.entity.Column;
import com.viettel.solution.extraction_service.entity.DatabaseConfig;
import com.viettel.solution.extraction_service.service.ColumnService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/column")
public class ColumnController {

    @Autowired
    private ColumnService columnService;

    @GetMapping
    public ResponseEntity<?> getColumn(@ModelAttribute RequestDto requestDto) {

        if (requestDto.getColumn().isBlank()) {
            return ResponseEntity.badRequest().body("Column name is required");
        }
        if (requestDto.getTable().isBlank()) {
            return ResponseEntity.badRequest().body("Table name is required");
        }

        ColumnDto column = columnService.getColumn(requestDto);

        if (column == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(column);
    }

    @PostMapping
    public ResponseEntity<Boolean> addColumn(@RequestBody @Valid ColumnDto column) {

        boolean flag = columnService.addColumn(column);

        return ResponseEntity.ok(flag);
    }

    @DeleteMapping
    public ResponseEntity<Column> deleteColumn() {
        return null;
    }

}
