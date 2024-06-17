package com.viettel.solution.extraction_service.service;

import com.viettel.solution.extraction_service.dto.RequestDto;
import com.viettel.solution.extraction_service.dto.SearchingDto;
import com.viettel.solution.extraction_service.entity.Table;
import com.viettel.solution.extraction_service.entity.TableDocument;

import java.util.List;

public interface TableService {
    public List<Table> getAllName(RequestDto requestDto);

    public List<TableDocument> findTables(SearchingDto searchDto);

    public Iterable<TableDocument> saveAllTableDocuments(List<TableDocument> tableDocuments);



}
