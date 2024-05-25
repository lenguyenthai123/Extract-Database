package com.viettel.solution.extraction_service.repository.impl;

import com.viettel.solution.extraction_service.entity.*;
import com.viettel.solution.extraction_service.repository.ConstraintRepository;
import com.viettel.solution.extraction_service.repository.TableRepository;
import org.checkerframework.checker.units.qual.C;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;


@Repository
public class TableRepositoryImpl implements TableRepository {

    @Autowired
    private ConstraintRepository constraintRepository;
    @Autowired
    private IndexRepositoryImpl indexRepository;
    @Autowired
    private TriggerRepositoryImpl triggerRepository;

    @Override
    public Table getTable(Connection connection, String databaseName, String schemaName, String tableName) {

        try {
            DatabaseMetaData metaData = connection.getMetaData();
            Table tableEntity = new Table();
            tableEntity.setName(tableName);

            // Lấy toàn bộ các constraint trong bảng.
            List<Constraint> constraints = constraintRepository.getAllConstraint(connection, databaseName, schemaName, tableName);
            if (constraints != null) {
                tableEntity.setConstraints(constraints);
            }

            // Lấy toàn bộ các index trong bảng.
            List<Index> indexs = indexRepository.getAllIndex(connection, databaseName, schemaName, tableName);
            if (indexs != null) {
                tableEntity.setIndexs(indexs);
            }

            // Lấy trigger liên quan đến bảng này
            List<Trigger> triggers = triggerRepository.getAllTrigger(connection, databaseName, schemaName, tableName);
            if (triggers != null) {
                tableEntity.setTriggers(triggers);
            }

            // Lấy thông tin các khóa chính của bảng để xác định cột nào là khóa chính
            Set<String> primaryKeys = new HashSet<>();
            ResultSet primaryKeysResultSet = metaData.getPrimaryKeys(databaseName, schemaName, tableName);
            while (primaryKeysResultSet.next()) {
                String primaryKey = primaryKeysResultSet.getString("COLUMN_NAME");
                primaryKeys.add(primaryKey);
            }

            // Lấy các cột trong bảng
            ResultSet columnsResultSet = metaData.getColumns(databaseName, schemaName, tableName, "%");
            while (columnsResultSet.next()) {
                String columnName = columnsResultSet.getString("COLUMN_NAME");
                String dataType = columnsResultSet.getString("TYPE_NAME");
                Integer columnSize = columnsResultSet.getInt("COLUMN_SIZE");

                // Kiểm tra cột có phải là khóa chính không
                Boolean isPrimaryKey = primaryKeys.contains(columnName);

                Column column = Column.builder()
                        .name(columnName)
                        .dataType(dataType)
                        .size(columnSize)
                        .isPrimaryKey(isPrimaryKey)
                        .build();

                tableEntity.getColumns().add(column);
            }
            return tableEntity;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Table> getAllTable(Connection connection, String databaseName, String schemaName) {
        try {
            List<Table> tables = new ArrayList<>();
            DatabaseMetaData metaData = connection.getMetaData();
            if (metaData == null) {
                return null;
            }
            ResultSet tablesResultSet = metaData.getTables(databaseName, schemaName, "%", new String[]{"TABLE"});

            while (tablesResultSet.next()) {
                Map<String, Object> table = new HashMap<>();
                String tableName = tablesResultSet.getString("TABLE_NAME");
                if (tableName.equals("sys_config")) {
                    continue;
                }
                Table tableEntity = getTable(connection, databaseName, schemaName, tableName);
                tables.add(tableEntity);
            }
            return tables;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
