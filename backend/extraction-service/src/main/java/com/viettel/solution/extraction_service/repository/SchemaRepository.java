package com.viettel.solution.extraction_service.repository;

import com.viettel.solution.extraction_service.entity.DatabaseStructure;
import com.viettel.solution.extraction_service.entity.Schema;

import java.sql.Connection;
import java.sql.DatabaseMetaData;

public interface SchemaRepository {

    //----------------------------------------------------------------------
    public Schema getSchema(Connection connection, String databaseName, String schemaName);
}
