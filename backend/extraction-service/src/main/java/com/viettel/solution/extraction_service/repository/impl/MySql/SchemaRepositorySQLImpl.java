package com.viettel.solution.extraction_service.repository.impl.MySql;

import com.viettel.solution.extraction_service.database.DatabaseConnection;
import com.viettel.solution.extraction_service.entity.Schema;
import com.viettel.solution.extraction_service.entity.Table;
import com.viettel.solution.extraction_service.repository.SchemaRepository;
import com.viettel.solution.extraction_service.repository.TableRepository;
import com.viettel.solution.extraction_service.repository.TriggerRepository;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.quartz.QuartzDataSource;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


@Repository
@Qualifier("mysql")
public class SchemaRepositorySQLImpl implements SchemaRepository {


    private TableRepository tableRepository;
    @Autowired
    public SchemaRepositorySQLImpl(@Qualifier("tableRepositorySQLImpl") TableRepository tableRepository) {
        this.tableRepository = tableRepository;
    }

    @Override
    public Schema getSchema(SessionFactory sessionFactory, String databaseName, String schemaName) {
        DatabaseMetaData metaData = DatabaseConnection.getDatabaseMetaData(sessionFactory);
        if (metaData == null) {
            return null;
        }
        // Lấy thông tin tên của các schema
        List<String> schemaNames = getAllSchemaName(sessionFactory);

        for (String name : schemaNames) {
            if (name.equals(schemaName)) {
                Schema schema = new Schema();
                schema.setName(name);
                // Lấy thông tin bảng trong schema
                List<Table> tables = tableRepository.getAllTable(sessionFactory, databaseName, schemaName);
                schema.setTables(tables);

                return schema;
            }
        }
        return null;
    }


    @Override
    public List<Schema> getAllSchema(SessionFactory sessionFactory, String databaseName) {
        List<Schema> schemas = new ArrayList<>();
        DatabaseMetaData metaData = DatabaseConnection.getDatabaseMetaData(sessionFactory);
        if (metaData == null) {
            return null;
        }

        // Lấy thông tin tên của các schema
        List<String> schemaNames = getAllSchemaName(sessionFactory);

        for (String schemaName : schemaNames) {
            Schema schema = new Schema();
            schema.setName(schemaName);
            // Lấy thông tin bảng trong schema
            List<Table> tables = tableRepository.getAllTable(sessionFactory, schemaName, null);
            schema.setTables(tables);
            schemas.add(schema);
        }
        return schemas;
    }


    @Override
    public List<String> getAllSchemaName(SessionFactory sessionFactory) {
        List<String> schemaNames = new ArrayList<>();
        ResultSet resultSet = null;

        try {
            DatabaseMetaData metaData = DatabaseConnection.getDatabaseMetaData(sessionFactory);
            resultSet = metaData.getCatalogs();

            while (resultSet.next()) {
                String databaseName = resultSet.getString(1);
                schemaNames.add(databaseName);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Xử lý ngoại lệ theo cách bạn mong muốn
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return schemaNames;
    }
}
