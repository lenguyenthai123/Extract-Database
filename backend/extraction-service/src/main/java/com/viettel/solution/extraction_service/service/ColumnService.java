package com.viettel.solution.extraction_service.service;

import com.viettel.solution.extraction_service.dto.ColumnDto;
import com.viettel.solution.extraction_service.dto.RequestDto;
import com.viettel.solution.extraction_service.entity.Column;

import java.util.List;

public interface ColumnService {
    ColumnDto getColumn(RequestDto requestDto);

    List<ColumnDto> getAllColumn(RequestDto requestDto);

    boolean updateColumn(ColumnDto column);

    boolean deleteColumn(ColumnDto column);

    boolean addColumn(ColumnDto column);
}
