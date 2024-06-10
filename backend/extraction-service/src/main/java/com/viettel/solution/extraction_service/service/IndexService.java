package com.viettel.solution.extraction_service.service;

import com.viettel.solution.extraction_service.dto.IndexDto;
import com.viettel.solution.extraction_service.entity.Index;

import java.util.List;

public interface IndexService {

    public IndexDto save(String type, String usernameId, IndexDto indexDto);

    public List<IndexDto> getListFromTable(String type, String usernameId, String schemaName, String tableName);

    public IndexDto update(String type, String usernameId, IndexDto indexDto);

    public boolean delete(String type, String usernameId, IndexDto indexDto);
}
