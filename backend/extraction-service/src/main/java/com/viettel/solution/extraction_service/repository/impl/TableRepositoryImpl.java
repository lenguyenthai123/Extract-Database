package com.viettel.solution.extraction_service.repository.impl;

import com.viettel.solution.extraction_service.entity.Column;
import com.viettel.solution.extraction_service.entity.Constraint;
import com.viettel.solution.extraction_service.entity.DatabaseConfig;
import com.viettel.solution.extraction_service.entity.Table;
import com.viettel.solution.extraction_service.repository.TableRepository;
import com.viettel.solution.extraction_service.utils.Utils;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

@Repository
public class TableRepositoryImpl implements TableRepository {

    @Override
    public Table getTableStructure(Connection connection, String databaseName, String schemaName, String tableName) {
        Table tableEntity = new Table();
        tableEntity.setName(tableName);

        String columnsQuery = "SELECT COLUMN_NAME, DATA_TYPE, CHARACTER_MAXIMUM_LENGTH " +
                "FROM INFORMATION_SCHEMA.COLUMNS " +
                "WHERE TABLE_SCHEMA = ? AND TABLE_NAME = ?";

        String primaryKeysQuery = "SELECT COLUMN_NAME, CONSTRAINT_NAME " +
                "FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE " +
                "WHERE TABLE_SCHEMA = ? AND TABLE_NAME = ? AND CONSTRAINT_NAME = 'PRIMARY'";

        String foreignKeysQuery = "SELECT COLUMN_NAME, CONSTRAINT_NAME, REFERENCED_TABLE_NAME, REFERENCED_COLUMN_NAME " +
                "FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE " +
                "WHERE TABLE_SCHEMA = ? AND TABLE_NAME = ? AND REFERENCED_TABLE_NAME IS NOT NULL";

        try {
            // Lấy thông tin các cột
            try (PreparedStatement columnsStmt = connection.prepareStatement(columnsQuery)) {
                columnsStmt.setString(1, schemaName);
                columnsStmt.setString(2, tableName);
                try (ResultSet columnsResultSet = columnsStmt.executeQuery()) {
                    while (columnsResultSet.next()) {
                        String columnName = columnsResultSet.getString("COLUMN_NAME");
                        String dataType = columnsResultSet.getString("DATA_TYPE");
                        Integer columnSize = columnsResultSet.getInt("CHARACTER_MAXIMUM_LENGTH");

                        Column column = Column.builder()
                                .name(columnName)
                                .dataType(dataType)
                                .size(columnSize)
                                .build();

                        tableEntity.getColumns().add(column);
                    }
                }
            }

            // Lấy thông tin các khóa chính
            Set<String> primaryKeys = new HashSet<>();
            try (PreparedStatement primaryKeysStmt = connection.prepareStatement(primaryKeysQuery)) {
                primaryKeysStmt.setString(1, schemaName);
                primaryKeysStmt.setString(2, tableName);
                try (ResultSet primaryKeysResultSet = primaryKeysStmt.executeQuery()) {
                    while (primaryKeysResultSet.next()) {
                        String primaryKey = primaryKeysResultSet.getString("COLUMN_NAME");
                        primaryKeys.add(primaryKey);

                        Constraint constraint = Constraint.builder()
                                .name(primaryKeysResultSet.getString("CONSTRAINT_NAME"))
                                .tableName(tableName)
                                .columnName(primaryKey)
                                .constraintType("PRIMARY KEY")
                                .build();
                    }
                }
            }

            // Cập nhật thông tin khóa chính cho các cột
            for (Column column : tableEntity.getColumns()) {
                if (primaryKeys.contains(column.getName())) {
                    column.setPrimaryKey(true);
                }
            }

            // Lấy thông tin các khóa ngoại
            Set<String> foreignKeys = new HashSet<>();
            try (PreparedStatement foreignKeysStmt = connection.prepareStatement(foreignKeysQuery)) {
                foreignKeysStmt.setString(1, schemaName);
                foreignKeysStmt.setString(2, tableName);
                try (ResultSet foreignKeysResultSet = foreignKeysStmt.executeQuery()) {
                    while (foreignKeysResultSet.next()) {
                        String foreignKey = foreignKeysResultSet.getString("COLUMN_NAME");
                        foreignKeys.add(foreignKey);

                        Constraint constraint = Constraint.builder()
                                .name(foreignKeysResultSet.getString("CONSTRAINT_NAME"))
                                .tableName(tableName)
                                .columnName(foreignKey)
                                .refTableName(foreignKeysResultSet.getString("REFERENCED_TABLE_NAME"))
                                .refColumnName(foreignKeysResultSet.getString("REFERENCED_COLUMN_NAME"))
                                .constraintType("FOREIGN KEY")
                                .build();
                    }
                }
            }
            return tableEntity;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
