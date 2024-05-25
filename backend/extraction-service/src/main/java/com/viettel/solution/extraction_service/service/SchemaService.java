package com.viettel.solution.extraction_service.service;

import com.viettel.solution.extraction_service.dto.RequestDto;
import com.viettel.solution.extraction_service.entity.Table;

public interface SchemaService {
    public Table getSchemaStructure(RequestDto requestDto);
}
