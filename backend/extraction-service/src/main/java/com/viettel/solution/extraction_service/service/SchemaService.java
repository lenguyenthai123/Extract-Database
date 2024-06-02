package com.viettel.solution.extraction_service.service;

import com.viettel.solution.extraction_service.dto.RequestDto;
import com.viettel.solution.extraction_service.entity.Table;

import java.util.List;

public interface SchemaService {
    public List<String> getAllName(RequestDto requestDto);
}
