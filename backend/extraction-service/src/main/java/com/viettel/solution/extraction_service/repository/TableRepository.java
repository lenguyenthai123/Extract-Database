package com.viettel.solution.extraction_service.repository;

import com.viettel.solution.extraction_service.entity.Table;

import java.sql.Connection;
import java.util.List;

public interface TableRepository {


    public Table getTable(Connection connection, String databaseName, String schemaName, String tableName);

    public List<Table> getAllTable(Connection connection, String databaseName, String schemaName);

}
