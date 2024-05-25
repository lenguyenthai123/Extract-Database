package com.viettel.solution.extraction_service.repository.impl;

import com.viettel.solution.extraction_service.entity.Column;
import com.viettel.solution.extraction_service.entity.Constraint;
import com.viettel.solution.extraction_service.entity.DatabaseConfig;
import com.viettel.solution.extraction_service.entity.Table;
import com.viettel.solution.extraction_service.repository.TableRepository;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

@Repository
public class TableRepositoryImpl implements TableRepository {
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
    public Table getTableStructure(Connection connection, String tableName) {
        try {
            DatabaseMetaData metaData = getDatabaseMetaData(connection);

            Table tableEntity = new Table();
            tableEntity.setName(tableName);

            ResultSet columnsResultSet = metaData.getColumns(null, null, tableName, "%");

            // Lấy thông tin các khóa chính
            Set<String> primaryKeys = new HashSet<>();
            ResultSet primaryKeysResultSet = metaData.getPrimaryKeys(null, null, tableName);
            while (primaryKeysResultSet.next()) {
                String primaryKey = primaryKeysResultSet.getString("COLUMN_NAME");
                primaryKeys.add(primaryKey);

                Constraint constraint = Constraint.builder()
                        .name(primaryKeysResultSet.getString("PK_NAME"))
                        .tableName(tableName)
                        .columnName(primaryKey)
                        .constraintType("PRIMARY KEY")
                        .build();
            }

            // Lấy thông tin các khóa ngoại
            Set<String> foreignKeys = new HashSet<>();
            ResultSet foreignKeysResultSet = metaData.getImportedKeys(null, null, tableName);
            while (foreignKeysResultSet.next()) {
                String foreignKey = foreignKeysResultSet.getString("FKCOLUMN_NAME");
                foreignKeys.add(foreignKey);

                Constraint constraint = Constraint.builder()
                        .name(foreignKeysResultSet.getString("FK_NAME"))
                        .tableName(tableName)
                        .columnName(foreignKey)
                        .refTableName(foreignKeysResultSet.getString("PKTABLE_NAME"))
                        .refColumnName(foreignKeysResultSet.getString("PKCOLUMN_NAME"))
                        .constraintType("FOREIGN KEY")
                        .build();
            }

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
}
