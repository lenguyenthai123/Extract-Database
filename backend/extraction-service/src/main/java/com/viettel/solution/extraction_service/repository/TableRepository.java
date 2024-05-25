package com.viettel.solution.extraction_service.repository;

import com.viettel.solution.extraction_service.entity.Column;
import com.viettel.solution.extraction_service.entity.DatabaseConfig;
import com.viettel.solution.extraction_service.entity.DatabaseStructure;
import com.viettel.solution.extraction_service.entity.Table;

import java.sql.Connection;
import java.sql.DatabaseMetaData;

public interface TableRepository {


    public Table getTableStructure(Connection connection, String databaseName, String schemaName, String tableName);


}
