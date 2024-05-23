package com.viettel.solution.extraction_service.repository;

import com.viettel.solution.extraction_service.entity.DatabaseConfigEntity;
import com.viettel.solution.extraction_service.entity.DatabaseEntity;
import com.viettel.solution.extraction_service.repository.impl.MySQLDatabaseRepository;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.util.ArrayList;
import java.util.List;

public interface DatabaseRepository {


    public DatabaseEntity getDatabaseStructure(DatabaseConfigEntity databaseConfigEntity);

    public DatabaseMetaData getDatabaseMetaData(Connection connection);

    public Connection connect(DatabaseConfigEntity databaseConfigEntity);

    public void disconnect(Connection connection);


    public static DatabaseRepository getPrototype(String databaseType) {
        if (databaseType.equals("mysql")) {
            return new MySQLDatabaseRepository();
        }
        return null;
    }
}
