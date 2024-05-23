package com.viettel.solution.extraction_service.service;

import com.viettel.solution.extraction_service.entity.DatabaseConfigEntity;
import com.viettel.solution.extraction_service.entity.DatabaseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

public interface ExtractionService {

    public DatabaseEntity getDatabaseStructure(DatabaseConfigEntity databaseConfigEntity);
}
