package com.viettel.solution.extraction_service.controller;

import com.viettel.solution.extraction_service.dto.RequestDto;
import com.viettel.solution.extraction_service.dto.TriggerDto;
import com.viettel.solution.extraction_service.entity.Trigger;
import com.viettel.solution.extraction_service.service.TriggerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/trigger")
public class TriggerController {

    @Autowired
    private TriggerService triggerService;

    @GetMapping("/list-from-table")
    public ResponseEntity<List<TriggerDto>> getTriggerListFromTable(@RequestParam String usernameId,
                                                                    @RequestParam String type,
                                                                    @RequestParam String schemaName,
                                                                    @RequestParam String tableName) {
        List<TriggerDto> triggerDtos = triggerService.getTriggerListFromTable(type, usernameId, schemaName, tableName);

        if (triggerDtos == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(triggerDtos);
        }
    }

    @PostMapping
    public ResponseEntity<Boolean> save(@Valid @RequestBody TriggerDto triggerDto) {
        TriggerDto savedTrigger = triggerService.save(triggerDto.getType(), triggerDto.getUsernameId(), triggerDto);

        if (savedTrigger != null) {
            return ResponseEntity.ok(true);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping
    public ResponseEntity<Boolean> updateTrigger(@Valid @RequestBody TriggerDto triggerDto) {
        TriggerDto updatedTrigger = triggerService.update(triggerDto.getType(), triggerDto.getUsernameId(), triggerDto);

        if (updatedTrigger != null) {
            return ResponseEntity.ok(true);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping
    public ResponseEntity<Boolean> deleteTrigger(@RequestParam String usernameId,
                                                 @RequestParam String type,
                                                 @RequestParam String schemaName,
                                                 @RequestParam String tableName,
                                                 @RequestParam String triggerName) {
        boolean isDelete = triggerService.delete(type, usernameId, schemaName, tableName, triggerName);

        if (isDelete) {
            return ResponseEntity.ok(isDelete);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }
}
