package com.viettel.solution.extraction_service.repository.impl;

import com.viettel.solution.extraction_service.entity.Schema;
import com.viettel.solution.extraction_service.entity.Table;
import com.viettel.solution.extraction_service.repository.SchemaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


@Service
public class SchemaRepositoryImpl implements SchemaRepository {


    @Autowired
    private TableRepositoryImpl tableRepository;

    @Override
    public Schema getSchema(Connection connection, String databaseName, String schemaName) {
        Schema schema = new Schema();
        try {
            DatabaseMetaData metaData = connection.getMetaData();
            if (metaData == null) {
                return null;
            }
            // Lấy thông tin schema
            try (ResultSet schemas = metaData.getSchemas()) {
                while (schemas.next()) {
                    String currentSchemaName = schemas.getString("TABLE_SCHEM");
                    if (currentSchemaName.equals(schemaName)) {

                        schema.setName(currentSchemaName);
                        // Lấy thông tin bảng trong schema
                        List<Table> tables = tableRepository.getAllTable(connection, databaseName, schemaName);
                        schema.setTables(tables);
                        break;
                    }
                }
            }
            return schema;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return schema;
    }


    @Override
    public List<Schema> getAllSchema(Connection connection, String databaseName) {
        List<Schema> schemas = new ArrayList<>();
        try {
            DatabaseMetaData metaData = connection.getMetaData();
            if (metaData == null) {
                return null;
            }

            // Lấy thông tin schema
            try (ResultSet schemasResultSet = metaData.getSchemas()) {
                while (schemasResultSet.next()) {
                    String schemaName = schemasResultSet.getString("TABLE_SCHEM");
                    Schema schema = new Schema();
                    schema.setName(schemaName);
                    // Lấy thông tin bảng trong schema
                    List<Table> tables = tableRepository.getAllTable(connection, databaseName, schemaName);
                    schema.setTables(tables);
                }
            }
            return schemas;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return schemas;
    }
}
