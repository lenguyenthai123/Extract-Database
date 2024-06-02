package com.viettel.solution.extraction_service.repository.impl.MySql;

import com.viettel.solution.extraction_service.database.DatabaseConnection;
import com.viettel.solution.extraction_service.entity.*;
import com.viettel.solution.extraction_service.repository.ConstraintRepository;
import com.viettel.solution.extraction_service.repository.IndexRepository;
import com.viettel.solution.extraction_service.repository.TableRepository;
import com.viettel.solution.extraction_service.repository.TriggerRepository;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;


@Repository
@Qualifier("tableRepositorySQLImpl")
public class TableRepositorySQLImpl implements TableRepository {

    @Autowired
    private ConstraintRepository constraintRepository;
    @Autowired
    private IndexRepository indexRepository;


    private TriggerRepository triggerRepository;

    @Autowired
    public TableRepositorySQLImpl(@Qualifier("triggerRepositorySQLImpl") TriggerRepository triggerRepository) {
        this.triggerRepository = triggerRepository;
    }

    @Override
    public Table getTable(SessionFactory sessionFactory, String databaseName, String schemaName, String tableName) {

        try {
            DatabaseMetaData metaData = DatabaseConnection.getDatabaseMetaData(sessionFactory);
            Table tableEntity = new Table();
            tableEntity.setName(tableName);

            // Lấy toàn bộ các constraint trong bảng.
            List<Constraint> constraints = constraintRepository.getAllConstraint(sessionFactory, databaseName, schemaName, tableName);
            if (constraints != null) {
                tableEntity.setConstraints(constraints);
            }

            // Lấy toàn bộ các index trong bảng.
            List<Index> indexs = indexRepository.getAllIndex(sessionFactory, databaseName, schemaName, tableName);
            if (indexs != null) {
                tableEntity.setIndexs(indexs);
            }

            // Lấy trigger liên quan đến bảng này
            List<Trigger> triggers = triggerRepository.getAllTrigger(sessionFactory, databaseName, schemaName, tableName);
            if (triggers != null) {
                tableEntity.setTriggers(triggers);
            }

            // Lấy thông tin các khóa chính của bảng để xác định cột nào là khóa chính
            Set<String> primaryKeys = new HashSet<>();
            try (ResultSet primaryKeysResultSet = metaData.getPrimaryKeys(databaseName, schemaName, tableName)) {
                while (primaryKeysResultSet.next()) {
                    String primaryKey = primaryKeysResultSet.getString("COLUMN_NAME");
                    primaryKeys.add(primaryKey);
                }
            }

            // Lấy các cột trong bảng
            try (ResultSet columnsResultSet = metaData.getColumns(databaseName, schemaName, tableName, "%")) {
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
            }
            return tableEntity;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Table> getAllTable(SessionFactory sessionFactory, String databaseName, String schemaName) {
        try {
            List<Table> tables = new ArrayList<>();
            DatabaseMetaData metaData = DatabaseConnection.getDatabaseMetaData(sessionFactory);
            if (metaData == null) {
                return null;
            }
            try (ResultSet tablesResultSet = metaData.getTables(databaseName, schemaName, "%", new String[]{"TABLE"})) {

                while (tablesResultSet.next()) {
                    String tableName = tablesResultSet.getString("TABLE_NAME");
                    String description = tablesResultSet.getString("REMARKS");
                    if (tableName.equals("sys_config")) {
                        continue;
                    }
                    Table tableEntity = getTable(sessionFactory, databaseName, schemaName, tableName);
                    tableEntity.setDescription(description);
                    tables.add(tableEntity);
                }
                return tables;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Table> getAllTableName(SessionFactory sessionFactory, String databaseName, String schemaName) {
        try {
            List<Table> tables = new ArrayList<>();
            DatabaseMetaData metaData = DatabaseConnection.getDatabaseMetaData(sessionFactory);
            if (metaData == null) {
                return null;
            }
            try (ResultSet tablesResultSet = metaData.getTables(databaseName, schemaName, "%", new String[]{"TABLE"})) {

                while (tablesResultSet.next()) {
                    String tableName = tablesResultSet.getString("TABLE_NAME");
                    String description = tablesResultSet.getString("REMARKS");
                    if (tableName.equals("sys_config")) {
                        continue;
                    }
                    Table table = Table.builder().name(tableName).description(description).build();
                    tables.add(table);
                }
                return tables;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
