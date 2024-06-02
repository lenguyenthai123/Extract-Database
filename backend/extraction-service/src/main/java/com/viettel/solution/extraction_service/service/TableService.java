package com.viettel.solution.extraction_service.service;

import com.viettel.solution.extraction_service.dto.RequestDto;
import com.viettel.solution.extraction_service.entity.Table;

import java.util.List;

public interface TableService {
    public List<Table> getAllName(RequestDto requestDto);

}
