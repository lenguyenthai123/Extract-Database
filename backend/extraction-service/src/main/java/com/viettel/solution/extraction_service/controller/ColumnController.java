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

import java.util.List;

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
        if (requestDto.getTableName().isBlank()) {
            return ResponseEntity.badRequest().body("Table name is required");
        }

        ColumnDto column = columnService.getColumn(requestDto);

        if (column == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(column);
    }

    @GetMapping("/list")
    public ResponseEntity<?> getAllColumn(@ModelAttribute RequestDto requestDto) {
        if (requestDto.getTableName().isBlank()) {
            return ResponseEntity.badRequest().body("Table name is required");
        }

        List<ColumnDto> columns = columnService.getAllColumn(requestDto);

        if (columns == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(columns);
    }

    @PostMapping
    public ResponseEntity<Boolean> addColumn(@RequestBody @Valid ColumnDto column) {

        boolean flag = columnService.addColumn(column);

        return ResponseEntity.ok(flag);
    }

    @PutMapping
    public ResponseEntity<Boolean> updateColumn(@RequestBody @Valid ColumnDto column) {

        if (column.getOldName().isBlank()) {
            throw new RuntimeException("Column name for identify is required");
        }

        boolean flag = columnService.updateColumn(column);

        return ResponseEntity.ok(flag);
    }

    @DeleteMapping
    public ResponseEntity<Boolean> deleteColumn(@ModelAttribute ColumnDto column) {

        boolean flag = columnService.deleteColumn(column);

        return ResponseEntity.ok(flag);
    }

}
