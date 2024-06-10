package com.viettel.solution.extraction_service.controller;

import com.viettel.solution.extraction_service.dto.ConstraintDto;
import com.viettel.solution.extraction_service.dto.IndexDto;
import com.viettel.solution.extraction_service.service.ConstraintService;
import com.viettel.solution.extraction_service.service.IndexService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/constraint")
public class ConstraintController {

    @Autowired
    private ConstraintService constraintService;

    @GetMapping("/list-from-table")
    public ResponseEntity<List<ConstraintDto>> getListFromTable(@RequestParam String type,
                                                                @RequestParam String usernameId,
                                                                @RequestParam String schemaName,
                                                                @RequestParam String tableName) {
        List<ConstraintDto> constraintDtos = constraintService.getListFromTable(type, usernameId, schemaName, tableName);

        return ResponseEntity.ok(constraintDtos);
    }

    @PostMapping
    public ResponseEntity<Boolean> save(@RequestBody @Valid ConstraintDto constraintDto) {
        ConstraintDto savedConstraint = constraintService.save(constraintDto.getType(), constraintDto.getUsernameId(), constraintDto);

        if (savedConstraint == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(true);
    }

    @PutMapping
    public ResponseEntity<Boolean> update(@RequestBody @Valid ConstraintDto constraintDto) {

        if (constraintDto.getOldName().isBlank()) {
            throw new RuntimeException("Old name is required");
        }
        ConstraintDto updatedConstraint = constraintService.update(constraintDto.getType(), constraintDto.getUsernameId(), constraintDto);
        if (updatedConstraint == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(true);
    }

    @DeleteMapping
    public ResponseEntity<Boolean> delete(@ModelAttribute ConstraintDto constraintDto) {
        boolean flag = constraintService.delete(constraintDto.getType(), constraintDto.getUsernameId(), constraintDto);
        return ResponseEntity.ok(flag);
    }
}
