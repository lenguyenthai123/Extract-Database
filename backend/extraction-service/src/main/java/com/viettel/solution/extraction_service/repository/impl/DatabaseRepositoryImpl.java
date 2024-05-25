package com.viettel.solution.extraction_service.repository.impl;

import com.viettel.solution.extraction_service.database.DatabaseConnection;
import com.viettel.solution.extraction_service.entity.Column;
import com.viettel.solution.extraction_service.entity.DatabaseConfig;
import com.viettel.solution.extraction_service.entity.DatabaseStructure;
import com.viettel.solution.extraction_service.entity.Table;
import com.viettel.solution.extraction_service.repository.DatabaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Repository
public class DatabaseRepositoryImpl implements DatabaseRepository {

    @Autowired
    private TableRepositoryImpl tableRepository;


    @Override
    public DatabaseMetaData getDatabaseMetaData(Connection connection) {
        try {
            if (connection == null) {
                return null;
            }
            DatabaseMetaData metaData = connection.getMetaData();
            return metaData;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public DatabaseStructure getDatabaseStructure(Connection connection, String databaseName) {
        try {
            DatabaseMetaData metaData = getDatabaseMetaData(connection);
            if (metaData == null) {
                return null;
            } else {

                DatabaseStructure databaseEntity = new DatabaseStructure();
                ResultSet tablesResultSet = metaData.getTables(null, null, "%", new String[]{"TABLE"});

                while (tablesResultSet.next()) {
                    Map<String, Object> table = new HashMap<>();
                    String tableName = tablesResultSet.getString("TABLE_NAME");
                    if (tableName.equals("sys_config")) {
                        continue;
                    }
                    Table tableEntity = tableRepository.getTableStructure(connection, tableName);

                    databaseEntity.getTables().add(tableEntity);
                }
                return databaseEntity;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
