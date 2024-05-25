package com.viettel.solution.extraction_service.repository.impl;

import com.viettel.solution.extraction_service.entity.Constraint;
import com.viettel.solution.extraction_service.repository.ConstraintRepository;
import org.hibernate.boot.Metadata;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ConstraintRepositoryImpl implements ConstraintRepository {

    @Override
    public Constraint getConstraint(Connection connection, String databaseName, String schemaName, String tableName, String constraintName) {
        try {
            DatabaseMetaData metaData = connection.getMetaData();
            if (metaData == null) {
                return null;
            }

            // Lấy thông tin các khóa chính
            ResultSet primaryKeysResultSet = metaData.getPrimaryKeys(databaseName, schemaName, tableName);
            while (primaryKeysResultSet.next()) {
                String primaryKey = primaryKeysResultSet.getString("COLUMN_NAME");
                if (primaryKey.equals(constraintName)) {
                    Constraint constraint = Constraint.builder()
                            .name(primaryKeysResultSet.getString("PK_NAME"))
                            .tableName(tableName)
                            .columnName(primaryKey)
                            .constraintType("PRIMARY KEY")
                            .build();
                    return constraint;
                }
            }

            // Lấy thông tin các khóa ngoại
            ResultSet foreignKeysResultSet = metaData.getImportedKeys(databaseName, schemaName, tableName);
            while (foreignKeysResultSet.next()) {
                String foreignKey = foreignKeysResultSet.getString("FKCOLUMN_NAME");
                if (foreignKey.equals(constraintName)) {
                    Constraint constraint = Constraint.builder()
                            .name(foreignKeysResultSet.getString("FK_NAME"))
                            .tableName(tableName)
                            .columnName(foreignKey)
                            .refTableName(foreignKeysResultSet.getString("PKTABLE_NAME"))
                            .refColumnName(foreignKeysResultSet.getString("PKCOLUMN_NAME"))
                            .constraintType("FOREIGN KEY")
                            .build();
                    return constraint;
                }
            }

            // Lấy thông tin các UNIQUE constraints
            ResultSet uniqueKeysResultSet = metaData.getIndexInfo(databaseName, schemaName, tableName, true, true);
            while (uniqueKeysResultSet.next()) {
                boolean nonUnique = uniqueKeysResultSet.getBoolean("NON_UNIQUE");
                String name = uniqueKeysResultSet.getString("INDEX_NAME");
                if (!nonUnique && name.equals(constraintName)) {
                    String uniqueKey = uniqueKeysResultSet.getString("COLUMN_NAME");
                    Constraint constraint = Constraint.builder()
                            .name(uniqueKeysResultSet.getString("INDEX_NAME"))
                            .tableName(tableName)
                            .columnName(uniqueKey)
                            .constraintType("UNIQUE")
                            .build();
                    return constraint;
                }
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Constraint> getAllConstraint(Connection connection, String databaseName, String schemaName, String tableName) {
        try {
            List<Constraint> constraints = new ArrayList<>();
            DatabaseMetaData metaData = connection.getMetaData();
            if (metaData == null) {
                return null;
            }

            // Lấy thông tin các khóa chính
            ResultSet primaryKeysResultSet = metaData.getPrimaryKeys(databaseName, schemaName, tableName);
            while (primaryKeysResultSet.next()) {
                String primaryKey = primaryKeysResultSet.getString("COLUMN_NAME");
                Constraint constraint = Constraint.builder()
                        .name(primaryKeysResultSet.getString("PK_NAME"))
                        .tableName(tableName)
                        .columnName(primaryKey)
                        .constraintType("PRIMARY KEY")
                        .build();
                constraints.add(constraint);
            }


            // Lấy thông tin các khóa ngoại
            ResultSet foreignKeysResultSet = metaData.getImportedKeys(databaseName, schemaName, tableName);
            while (foreignKeysResultSet.next()) {
                String foreignKey = foreignKeysResultSet.getString("FKCOLUMN_NAME");
                Constraint constraint = Constraint.builder()
                        .name(foreignKeysResultSet.getString("FK_NAME"))
                        .tableName(tableName)
                        .columnName(foreignKey)
                        .refTableName(foreignKeysResultSet.getString("PKTABLE_NAME"))
                        .refColumnName(foreignKeysResultSet.getString("PKCOLUMN_NAME"))
                        .constraintType("FOREIGN KEY")
                        .build();
                constraints.add(constraint);
            }

            // Lấy thông tin các UNIQUE constraints
            ResultSet uniqueKeysResultSet = metaData.getIndexInfo(databaseName, schemaName, tableName, true, true);
            while (uniqueKeysResultSet.next()) {
                boolean nonUnique = uniqueKeysResultSet.getBoolean("NON_UNIQUE");
                if (!nonUnique) {
                    String uniqueKey = uniqueKeysResultSet.getString("COLUMN_NAME");
                    Constraint constraint = Constraint.builder()
                            .name(uniqueKeysResultSet.getString("INDEX_NAME"))
                            .tableName(tableName)
                            .columnName(uniqueKey)
                            .constraintType("UNIQUE")
                            .build();
                    constraints.add(constraint);
                }
            }


            return constraints;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
