package com.viettel.solution.extraction_service.repository;

import com.viettel.solution.extraction_service.entity.Column;
import com.viettel.solution.extraction_service.entity.DatabaseConfig;
import com.viettel.solution.extraction_service.entity.DatabaseStructure;
import com.viettel.solution.extraction_service.entity.Table;
import com.viettel.solution.extraction_service.repository.impl.MySQLDatabaseRepository;

import java.sql.Connection;
import java.sql.DatabaseMetaData;

public interface DatabaseRepository {

    // Helper
    public DatabaseMetaData getDatabaseMetaData(Connection connection);

    public Connection connect(DatabaseConfig databaseConfigEntity);

    public void disconnect(Connection connection);


    public static DatabaseRepository getPrototype(String databaseType) {
        if (databaseType.equals("mysql")) {
            return new MySQLDatabaseRepository();
        }
        return null;
    }
    //----------------------------------------------------------------------

    public DatabaseStructure getDatabaseStructure(DatabaseConfig databaseConfigEntity);
    public Table getTableStructure(DatabaseConfig databaseConfigEntity, String tableName);
    public Column getColumnStructure(DatabaseConfig databaseConfigEntity, String tableName, String columnName);




}
