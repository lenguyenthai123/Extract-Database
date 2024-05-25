package com.viettel.solution.extraction_service.repository;

import com.viettel.solution.extraction_service.entity.DatabaseStructure;

import java.sql.Connection;
import java.sql.DatabaseMetaData;

public interface DatabaseRepository {

    public DatabaseStructure getDatabaseStructure(Connection connection, String databaseName);



}
