package com.viettel.solution.extraction_service.repository;

import com.viettel.solution.extraction_service.entity.Schema;

import java.sql.Connection;
import java.util.List;

public interface SchemaRepository {

    //----------------------------------------------------------------------
    public Schema getSchema(Connection connection, String databaseName, String schemaName);
    public List<Schema> getAllSchema(Connection connection, String databaseName);

}
