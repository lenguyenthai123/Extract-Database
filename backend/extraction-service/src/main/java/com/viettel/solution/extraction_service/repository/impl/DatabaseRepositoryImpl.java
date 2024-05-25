package com.viettel.solution.extraction_service.repository.impl;

import com.viettel.solution.extraction_service.entity.*;
import com.viettel.solution.extraction_service.repository.DatabaseRepository;
import com.viettel.solution.extraction_service.repository.SchemaRepository;
import com.viettel.solution.extraction_service.repository.TableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class DatabaseRepositoryImpl implements DatabaseRepository {

    @Autowired
    private SchemaRepository schemaRepository;

    @Autowired
    private TableRepository tableRepository;

    @Override
    public Database getDatabase(Connection connection, String databaseName) {
        try {
            DatabaseMetaData metaData = connection.getMetaData();
            if (metaData == null) {
                return null;
            } else {

                Database databaseEntity = new Database();
                databaseEntity.setName(databaseName);

                // Lấy thông tin các schema
                List<Schema> schemas = schemaRepository.getAllSchema(connection, databaseName);
                databaseEntity.setSchemas(schemas);

                return databaseEntity;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Database getSQLDatabase(Connection connection, String databaseName) {
        try {
            DatabaseMetaData metaData = connection.getMetaData();
            if (metaData == null) {
                return null;
            } else {

                // Lấy thông tin các schema
                List<Table> tables = tableRepository.getAllTable(connection, databaseName, null);


                return new Database(tables, databaseName);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
