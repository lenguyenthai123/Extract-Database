package com.viettel.solution.extraction_service.controller;


import com.viettel.solution.extraction_service.dto.RequestDto;
import com.viettel.solution.extraction_service.service.SchemaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/schema")
public class SchemaController {

    @Autowired
    private SchemaService schemaService;

    @GetMapping("/list")
    public ResponseEntity<List<String>> getList(@RequestParam String usernameId,
                                                @RequestParam String type) {
        RequestDto req = RequestDto.builder()
                .usernameId(usernameId)
                .type(type)
                .build();

        List<String> shemaNames = schemaService.getAllName(req);

        if (shemaNames != null) {
            return ResponseEntity.ok(shemaNames);
        }
        return ResponseEntity.notFound().build();
    }


}
