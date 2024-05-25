package com.viettel.solution.extraction_service.repository.impl;

import com.viettel.solution.extraction_service.entity.Schema;
import com.viettel.solution.extraction_service.entity.Table;
import com.viettel.solution.extraction_service.repository.SchemaRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class SchemaRepositoryImpl implements SchemaRepository {

    @Autowired
    private TableRepositoryImpl tableRepository;

    @Override
    public Schema getSchema(Connection connection, String databaseName, String schemaName) {
        Schema schema = new Schema();
        try {
            DatabaseMetaData metaData = connection.getMetaData();
            if(metaData == null) {
                return null;
            }

            // Lấy thông tin schema
            try (ResultSet schemas = metaData.getSchemas()) {
                while (schemas.next()) {
                    String currentSchemaName = schemas.getString("TABLE_SCHEM");
                    if (currentSchemaName.equals(schemaName)) {

                        schema.setName(currentSchemaName);
                        // Lấy thông tin bảng trong schema
                        try (ResultSet tablesResultSet = metaData.getTables(databaseName, schemaName, null, new String[]{"TABLE"})) {
                            while (tablesResultSet.next()) {
                                String tableName = tablesResultSet.getString("TABLE_NAME");
                                String tableType = tablesResultSet.getString("TABLE_TYPE");
                                if (tableName.equals("sys_config")) {
                                    continue;
                                }
                                Table table = tableRepository.getTableStructure(connection, databaseName, schemaName, tableName);
                                if (table != null) {
                                    schema.getTables().add(table);
                                }
                            }
                        }
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
}
