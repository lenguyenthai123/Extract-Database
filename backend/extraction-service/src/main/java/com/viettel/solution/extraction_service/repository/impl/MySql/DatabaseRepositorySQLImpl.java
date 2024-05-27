package com.viettel.solution.extraction_service.repository.impl.MySql;

import com.viettel.solution.extraction_service.database.DatabaseConnection;
import com.viettel.solution.extraction_service.entity.Database;
import com.viettel.solution.extraction_service.entity.Schema;
import com.viettel.solution.extraction_service.entity.Table;
import com.viettel.solution.extraction_service.repository.DatabaseRepository;
import com.viettel.solution.extraction_service.repository.SchemaRepository;
import com.viettel.solution.extraction_service.repository.TableRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
@Qualifier("databaseRepositorySQLImpl")
public class DatabaseRepositorySQLImpl implements DatabaseRepository {

    @Autowired
    private SchemaRepository schemaRepository;

    @Override
    public Database getDatabase(SessionFactory sessionFactory) {
        DatabaseMetaData metaData = DatabaseConnection.getDatabaseMetaData(sessionFactory);
        if (metaData == null) {
            return null;
        }
        List<Schema> schemas = schemaRepository.getAllSchema(sessionFactory, null);
        if (schemas == null) {
            return null;
        }
        Database database = new Database(schemas);

        return database;

    }

}
