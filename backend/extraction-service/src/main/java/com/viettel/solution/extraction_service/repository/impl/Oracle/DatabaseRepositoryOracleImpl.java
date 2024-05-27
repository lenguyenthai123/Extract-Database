package com.viettel.solution.extraction_service.repository.impl.Oracle;

import com.viettel.solution.extraction_service.database.DatabaseConnection;
import com.viettel.solution.extraction_service.entity.Database;
import com.viettel.solution.extraction_service.entity.Schema;
import com.viettel.solution.extraction_service.entity.Table;
import com.viettel.solution.extraction_service.repository.DatabaseRepository;
import com.viettel.solution.extraction_service.repository.SchemaRepository;
import com.viettel.solution.extraction_service.repository.TableRepository;
import com.viettel.solution.extraction_service.repository.TriggerRepository;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.List;

@Repository
@Qualifier("databaseRepositoryOracleImpl")
public class DatabaseRepositoryOracleImpl implements DatabaseRepository {

    private TableRepository tableRepository;

    @Autowired
    public DatabaseRepositoryOracleImpl(@Qualifier("tableRepositoryOracleImpl") TableRepository tableRepository) {
        this.tableRepository = tableRepository;
    }

    @Override
    public Database getDatabase(SessionFactory sessionFactory) {
        DatabaseMetaData metaData = DatabaseConnection.getDatabaseMetaData(sessionFactory);
        if (metaData == null) {
            return null;
        }
        String username = null;

        try {
            username = metaData.getUserName();
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<Table> tables = tableRepository.getAllTable(sessionFactory, null, username);
        if (tables == null) {
            return null;
        }
        Schema schema = new Schema(tables);
        Database database = new Database(schema);

        return database;
    }
}
