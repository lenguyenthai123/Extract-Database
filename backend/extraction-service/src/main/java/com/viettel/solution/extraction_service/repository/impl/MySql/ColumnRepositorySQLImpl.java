package com.viettel.solution.extraction_service.repository.impl.MySql;

import com.viettel.solution.extraction_service.database.DatabaseConnection;
import com.viettel.solution.extraction_service.dto.ColumnDto;
import com.viettel.solution.extraction_service.dto.RequestDto;
import com.viettel.solution.extraction_service.entity.*;
import com.viettel.solution.extraction_service.repository.ColumnRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.viettel.solution.extraction_service.repository.impl.CommonRepository.columnExists;
import static com.viettel.solution.extraction_service.repository.impl.CommonRepository.tableExists;


@Repository
@Qualifier("MySql")
public class ColumnRepositorySQLImpl implements ColumnRepository {


    @Override
    public Column getColumn(SessionFactory sessionFactory, RequestDto requestDto) {
        try {
            Column column = null;

            DatabaseMetaData metaData = DatabaseConnection.getDatabaseMetaData(sessionFactory);

            String databaseName = requestDto.getDatabaseName();
            String schemaName = requestDto.getSchemaName();
            String tableName = requestDto.getTableName();
            String columnName = requestDto.getColumn();

            // Lấy thông tin các khóa chính của bảng để xác định cột nào là khóa chính
            Set<String> primaryKeys = new HashSet<>();
            try (ResultSet primaryKeysResultSet = metaData.getPrimaryKeys(databaseName, schemaName, tableName)) {
                while (primaryKeysResultSet.next()) {
                    String primaryKey = primaryKeysResultSet.getString("COLUMN_NAME");
                    primaryKeys.add(primaryKey);
                }
            }

            // Lấy các cột trong bảng
            try (ResultSet columnsResultSet = metaData.getColumns(databaseName, schemaName, tableName, columnName)) {
                while (columnsResultSet.next()) {
                    String name = columnsResultSet.getString("COLUMN_NAME");
                    String dataType = columnsResultSet.getString("TYPE_NAME");
                    Integer columnSize = columnsResultSet.getInt("COLUMN_SIZE");

                    Boolean isNullable = "YES".equals(columnsResultSet.getString("IS_NULLABLE"));
                    Boolean isAutoIncrement = "YES".equals(columnsResultSet.getString("IS_AUTOINCREMENT"));
                    String defaultValue = columnsResultSet.getString("COLUMN_DEF");
                    String description = columnsResultSet.getString("REMARKS");

                    // Kiểm tra cột có phải là khóa chính không
                    Boolean isPrimaryKey = primaryKeys.contains(columnName);

                    column = Column.builder()
                            .name(columnName)
                            .dataType(dataType)
                            .size(columnSize)
                            .isPrimaryKey(isPrimaryKey)
                            .description(description)
                            .autoIncrement(isAutoIncrement)
                            .defaultValue(defaultValue)
                            .nullable(isNullable)
                            .build();

                }
            }
            return column;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Column> getAllColumn(SessionFactory sessionFactory, RequestDto requestDto) {
        try {
            List<Column> columns = new ArrayList<>();

            DatabaseMetaData metaData = DatabaseConnection.getDatabaseMetaData(sessionFactory);

            String databaseName = requestDto.getDatabaseName();
            String schemaName = requestDto.getSchemaName();
            String tableName = requestDto.getTableName();

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
                    String name = columnsResultSet.getString("COLUMN_NAME");
                    String dataType = columnsResultSet.getString("TYPE_NAME");
                    Integer columnSize = columnsResultSet.getInt("COLUMN_SIZE");
                    Boolean isNullable = "YES".equals(columnsResultSet.getString("IS_NULLABLE"));
                    Boolean isAutoIncrement = "YES".equals(columnsResultSet.getString("IS_AUTOINCREMENT"));
                    String defaultValue = columnsResultSet.getString("COLUMN_DEF");
                    String description = columnsResultSet.getString("REMARKS");

                    // Kiểm tra cột có phải là khóa chính không
                    Boolean isPrimaryKey = primaryKeys.contains(name);

                    Column column = Column.builder()
                            .name(name)
                            .dataType(dataType)
                            .size(columnSize)
                            .isPrimaryKey(isPrimaryKey)
                            .description(description)
                            .autoIncrement(isAutoIncrement)
                            .defaultValue(defaultValue)
                            .nullable(isNullable)
                            .build();

                    columns.add(column);
                }
            }
            return columns;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public boolean addColumn(SessionFactory sessionFactory, ColumnDto columnDto) {
        Session session = sessionFactory.openSession();
        try {
            if (!tableExists(session, columnDto.getSchemaName(), columnDto.getTableName())) {
                throw new RuntimeException("Table does not exist");
            }
            if (columnExists(session, columnDto.getSchemaName(), columnDto.getTableName(), columnDto.getName())) {
                throw new RuntimeException("Column already exists");
            }
            return addOrUpdateColumn(sessionFactory, columnDto, "ADD");
        } catch (RuntimeException e) {
            throw e;
        } finally {
            session.close();
        }
    }

    @Override
    public boolean updateColumn(SessionFactory sessionFactory, ColumnDto columnDto) {
        Session session = sessionFactory.openSession();
        try {
            if (!tableExists(session, columnDto.getSchemaName(), columnDto.getTableName())) {
                throw new RuntimeException("Table does not exist");
            }
            if (!columnExists(session, columnDto.getSchemaName(), columnDto.getTableName(), columnDto.getName())) {
                throw new RuntimeException("Column does not exist");
            }
            return addOrUpdateColumn(sessionFactory, columnDto, "MODIFY");
        } catch (RuntimeException e) {
            throw e;
        } finally {
            session.close();
        }
    }

    @Override
    public boolean deleteColumn(SessionFactory sessionFactory, ColumnDto columnDto) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();

            String schema = columnDto.getSchemaName();
            String tableName = columnDto.getTableName();
            String columnName = columnDto.getName();

            // Kiểm tra cột có tồn tại không
            if (!columnExists(session, schema, tableName, columnName)) {
                throw new RuntimeException("Column does not exist");
            }

            // Xóa cột
            String deleteColumnQuery = String.format("ALTER TABLE %s.%s DROP COLUMN %s", schema, tableName, columnName);
            session.createNativeQuery(deleteColumnQuery).executeUpdate();

            transaction.commit();
            return true;
        } catch (RuntimeException e) {
            transaction.rollback();
            throw e;
        } finally {
            session.close();
        }

    }


    private boolean addOrUpdateColumn(SessionFactory sessionFactory, ColumnDto columnDto, String action) {
        Session session = sessionFactory.openSession();

        Transaction transaction = null;

        boolean checkRollback = false;
        try {
            transaction = session.beginTransaction();

            String schema = columnDto.getSchemaName();
            String tableName = columnDto.getTableName();
            String columnName = columnDto.getName();
            String columnType = columnDto.getDataType();
            Integer size = columnDto.getSize();
            Boolean nullable = columnDto.isNullable();
            Boolean autoIncrement = columnDto.isAutoIncrement();
            String defaultValue = columnDto.getDefaultValue();
            Boolean primaryKey = columnDto.isPrimaryKey();
            String description = columnDto.getDescription();

            StringBuilder alterTableQuery = new StringBuilder();
            alterTableQuery.append(String.format("ALTER TABLE %s.%s %s COLUMN %s %s",
                    schema, tableName, action, columnName, columnType));

            if (size != null) {
                alterTableQuery.append(String.format("(%d)", size));
            }

            if (nullable != null && !nullable) {
                alterTableQuery.append(" NOT NULL");
            }

            if (defaultValue != null) {
                alterTableQuery.append(String.format(" DEFAULT '%s'", defaultValue));
            }

            if (autoIncrement != null && autoIncrement) {
                alterTableQuery.append(" AUTO_INCREMENT");
            }

            // Execute the query to add the column
            session.createNativeQuery(alterTableQuery.toString()).executeUpdate();
            checkRollback = true;

            // If the column should be a primary key, add it as a primary key
            if (primaryKey != null && primaryKey) {
                String primaryKeyQuery = String.format("ALTER TABLE %s.%s ADD PRIMARY KEY (%s)",
                        schema, tableName, columnName);
                session.createNativeQuery(primaryKeyQuery).executeUpdate();
            }


            // If description is provided, add the comment to the column
            if (description != null && !description.isEmpty()) {
                StringBuilder commentQuery = new StringBuilder();
                commentQuery.append(String.format("ALTER TABLE %s.%s MODIFY %s %s", schema, tableName, columnName, columnType));

                if (size != null) {
                    commentQuery.append(String.format("(%d)", size));
                }

                if (nullable != null && !nullable) {
                    commentQuery.append(" NOT NULL");
                }

                if (defaultValue != null) {
                    commentQuery.append(String.format(" DEFAULT '%s'", defaultValue));
                }

                if (autoIncrement != null && autoIncrement) {
                    commentQuery.append(" AUTO_INCREMENT");
                }

                commentQuery.append(String.format(" COMMENT '%s'", description));

                session.createNativeQuery(commentQuery.toString()).executeUpdate();
            }

            transaction.commit();
            return true;
        } catch (RuntimeException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            if (checkRollback) {
                deleteColumn(sessionFactory, columnDto);
            }
            throw e;
        } finally {
            session.close();
        }
    }


}
