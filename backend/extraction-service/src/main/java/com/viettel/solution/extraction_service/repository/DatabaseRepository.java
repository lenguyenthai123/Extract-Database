package com.viettel.solution.extraction_service.repository;

import com.viettel.solution.extraction_service.entity.Database;

import java.sql.Connection;

public interface DatabaseRepository {

    public Database getDatabase(Connection connection, String databaseName);

    public Database getSQLDatabase(Connection connection, String databaseName);


}
