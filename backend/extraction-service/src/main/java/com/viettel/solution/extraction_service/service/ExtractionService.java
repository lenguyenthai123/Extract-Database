package com.viettel.solution.extraction_service.service;

import com.viettel.solution.extraction_service.entity.Column;
import com.viettel.solution.extraction_service.entity.DatabaseConfig;
import com.viettel.solution.extraction_service.entity.DatabaseStructure;
import com.viettel.solution.extraction_service.entity.Table;

public interface ExtractionService {

    public DatabaseStructure getDatabaseStructure(DatabaseConfig databaseConfigEntity);

    public Table getTableStructure(DatabaseConfig databaseConfigEntity, String tableName);
    public Column getTableStructure(DatabaseConfig databaseConfigEntity, String tableName, String columnName);

}
