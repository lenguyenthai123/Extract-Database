package com.viettel.solution.extraction_service.controller;

import com.viettel.solution.extraction_service.dto.IndexDto;
import com.viettel.solution.extraction_service.service.IndexService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/index")
public class IndexController {

    @Autowired
    private IndexService indexService;

    @GetMapping("/list-from-table")
    public ResponseEntity<List<IndexDto>> getListFromTable(@RequestParam String type,
                                                           @RequestParam String usernameId,
                                                           @RequestParam String schemaName,
                                                           @RequestParam String tableName) {
        List<IndexDto> indexDtos = indexService.getListFromTable(type, usernameId, schemaName, tableName);

        return ResponseEntity.ok(indexDtos);
    }

    @PostMapping
    public ResponseEntity<Boolean> save(@RequestBody @Valid IndexDto indexDto) {
        IndexDto savedIndex = indexService.save(indexDto.getType(), indexDto.getUsernameId(), indexDto);

        if (savedIndex == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(true);
    }

    @PutMapping
    public ResponseEntity<Boolean> update(@RequestBody @Valid IndexDto indexDto) {

        if (indexDto.getOldName().isBlank()) {
            throw new RuntimeException("Old name is required");
        }
        IndexDto updatedIndex = indexService.update(indexDto.getType(), indexDto.getUsernameId(), indexDto);
        if (updatedIndex == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(true);
    }

    @DeleteMapping
    public ResponseEntity<Boolean> delete(@ModelAttribute IndexDto indexDto) {
        boolean flag = indexService.delete(indexDto.getType(), indexDto.getUsernameId(), indexDto);

        return ResponseEntity.ok(flag);
    }
}
