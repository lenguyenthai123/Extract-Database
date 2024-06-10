package com.viettel.solution.extraction_service.repository.impl.MySql;

import com.viettel.solution.extraction_service.database.DatabaseConnection;
import com.viettel.solution.extraction_service.dto.ColumnDto;
import com.viettel.solution.extraction_service.dto.RequestDto;
import com.viettel.solution.extraction_service.entity.Column;
import com.viettel.solution.extraction_service.repository.ColumnRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.exception.GenericJDBCException;
import org.hibernate.exception.SQLGrammarException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.viettel.solution.extraction_service.repository.impl.CommonRepository.columnExists;
import static com.viettel.solution.extraction_service.repository.impl.CommonRepository.tableExists;


@Repository
@Qualifier("MySql")
public class ColumnRepositorySQLImpl implements ColumnRepository {

    //https://dev.mysql.com/doc/refman/8.4/en/cannot-roll-back.html#:~:text=Some%20statements%20cannot%20be%20rolled,alter%20tables%20or%20stored%20routines.
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

                    column = Column.builder().name(columnName).dataType(dataType).size(columnSize).isPrimaryKey(isPrimaryKey).description(description).autoIncrement(isAutoIncrement).defaultValue(defaultValue).nullable(isNullable).build();

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

                    Column column = Column.builder().name(name).dataType(dataType).size(columnSize).isPrimaryKey(isPrimaryKey).description(description).autoIncrement(isAutoIncrement).defaultValue(defaultValue).nullable(isNullable).build();

                    columns.add(column);
                }
            }
            return columns;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public Column addColumn(SessionFactory sessionFactory, ColumnDto columnDto) {

        Session session = sessionFactory.openSession();

        Transaction transaction = null;

        boolean checkRollback = false;
        try {
            transaction = session.beginTransaction();

            String schema = columnDto.getSchemaName();
            String tableName = columnDto.getTableName();

            String oldName = columnDto.getOldName();
            String columnName = columnDto.getName();
            String columnType = columnDto.getDataType();
            Integer size = columnDto.getSize();
            Boolean nullable = columnDto.isNullable();
            Boolean autoIncrement = columnDto.isAutoIncrement();
            String defaultValue = columnDto.getDefaultValue();
            Boolean primaryKey = columnDto.isPrimaryKey();
            String description = columnDto.getDescription();

            StringBuilder alterTableQuery = new StringBuilder();
            alterTableQuery.append(String.format("ALTER TABLE %s.%s %s COLUMN %s %s", schema, tableName, "ADD", columnName, columnType));

            if (size != null) {
                alterTableQuery.append(String.format("(%d)", size));
            }

            if (defaultValue != null) {
                alterTableQuery.append(String.format(" DEFAULT '%s'", defaultValue));
            }

            if (nullable != null && !nullable) {
                alterTableQuery.append(" NOT NULL");
            } else {
                alterTableQuery.append(" NULL");
            }

            // Execute the query to add the column
            session.createNativeQuery(alterTableQuery.toString()).executeUpdate();
            checkRollback = true;

            if (autoIncrement != null && autoIncrement) {
                String autoIncrementQuery = String.format(
                        "ALTER TABLE %s.%s MODIFY COLUMN %s %s AUTO_INCREMENT",
                        schema, tableName, columnName, columnType
                );

                session.createNativeQuery(autoIncrementQuery).executeUpdate();
            }


            // If the column should be a primary key, add it as a primary key
            if (primaryKey != null && primaryKey) {
                String primaryKeyQuery = String.format("ALTER TABLE %s.%s ADD PRIMARY KEY (%s)", schema, tableName, columnName);
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

                if (autoIncrement != null && autoIncrement) {
                    commentQuery.append(" AUTO_INCREMENT");
                }

                commentQuery.append(String.format(" COMMENT '%s'", description));

                session.createNativeQuery(commentQuery.toString()).executeUpdate();
            }

            transaction.commit();
            return new Column(columnDto);
        } catch (GenericJDBCException | SQLGrammarException e) {
            session.getTransaction().rollback();
            if (checkRollback) {
                deleteColumn(sessionFactory, columnDto);
            }
            throw e;
        } finally {
            session.close();
        }
    }

    @Override
    public Column updateColumn(SessionFactory sessionFactory, ColumnDto columnDto) {


        Session session = null;
        Transaction transaction = null;
        boolean checkRollback = false;
        boolean afterAdd = false;
        boolean afterDelete = false;
        try {
            session = sessionFactory.openSession();
            DatabaseMetaData metaData = DatabaseConnection.getDatabaseMetaData(sessionFactory);

            transaction = session.beginTransaction();

            String schema = columnDto.getSchemaName();
            String tableName = columnDto.getTableName();

            String oldName = columnDto.getOldName();
            String columnName = columnDto.getName();
            String columnType = columnDto.getDataType();
            Integer size = columnDto.getSize();
            Boolean nullable = columnDto.isNullable();
            Boolean autoIncrement = columnDto.isAutoIncrement();
            String defaultValue = columnDto.getDefaultValue();
            Boolean primaryKey = columnDto.isPrimaryKey();
            String description = columnDto.getDescription();

            //Doi ten
            String renameColumnQuery = String.format(
                    "ALTER TABLE %s.%s RENAME COLUMN %s TO %s",
                    schema, tableName, oldName, columnName
            );
            session.createNativeQuery(renameColumnQuery).executeUpdate();


            StringBuilder alterColumnQuery = new StringBuilder();
            alterColumnQuery.append(String.format(
                    "ALTER TABLE %s.%s MODIFY COLUMN %s %s",
                    schema, tableName, columnName, columnType
            ));

            if (size != null) {
                alterColumnQuery.append(String.format("(%d)", size));
            }

            if (nullable != null && !nullable) {
                alterColumnQuery.append(" NOT NULL");
            } else {
                alterColumnQuery.append(" NULL");
            }

            if (defaultValue != null) {
                alterColumnQuery.append(String.format(" DEFAULT '%s'", defaultValue));
            }


            session.createNativeQuery(alterColumnQuery.toString()).executeUpdate();

            // Lấy thông tin các khóa chính của bảng để xác định cột nào là khóa chính
            Set<String> primaryKeys = new HashSet<>();
            try (ResultSet primaryKeysResultSet = metaData.getPrimaryKeys(null, schema, tableName)) {
                while (primaryKeysResultSet.next()) {
                    String namePrimaryKey = primaryKeysResultSet.getString("COLUMN_NAME");
                    primaryKeys.add(namePrimaryKey);
                }
            }

            // If the column should be a primary key, add it as a primary key
            if (primaryKey && !primaryKeys.contains(columnName)) {
                String primaryKeyQuery2 = String.format("ALTER TABLE %s.%s ADD PRIMARY KEY (%s)", schema, tableName, columnName);
                session.createNativeQuery(primaryKeyQuery2).executeUpdate();
            }
            // Trường hợp nó là primary key nhưng nó muốn bỏ primary key


            if (!primaryKey && primaryKeys.contains(columnName)) {
                String primaryKeyQuery1 = String.format("ALTER TABLE %s.%s DROP PRIMARY KEY", schema, tableName);
                session.createNativeQuery(primaryKeyQuery1).executeUpdate();

                for (String namePrimaryKey : primaryKeys) {
                    if (!namePrimaryKey.equals(columnName)) {
                        String primaryKeyQuery2 = String.format("ALTER TABLE %s.%s ADD PRIMARY KEY (%s)", schema, tableName, namePrimaryKey);
                        session.createNativeQuery(primaryKeyQuery2).executeUpdate();
                    }
                }

            }


            if (autoIncrement != null && autoIncrement) {
                String autoIncrementQuery = String.format(
                        "ALTER TABLE %s.%s MODIFY COLUMN %s %s AUTO_INCREMENT",
                        schema, tableName, columnName, columnType
                );

                session.createNativeQuery(autoIncrementQuery).executeUpdate();
            }

            checkRollback = true;


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

                if (autoIncrement != null && autoIncrement) {
                    commentQuery.append(" AUTO_INCREMENT");
                }

                commentQuery.append(String.format(" COMMENT '%s'", description));

                session.createNativeQuery(commentQuery.toString()).executeUpdate();


            }

            transaction.commit();
            return new Column(columnDto);
        } catch (GenericJDBCException | SQLGrammarException e) {

            if (checkRollback) {
                deleteColumn(sessionFactory, columnDto);
            }
            throw e;
        } catch (Exception e) {
            if (checkRollback) {
                deleteColumn(sessionFactory, columnDto);
            }
            e.printStackTrace();
            return null;
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

            // Xóa cột
            String deleteColumnQuery = String.format("ALTER TABLE %s.%s DROP COLUMN %s", schema, tableName, columnName);
            session.createNativeQuery(deleteColumnQuery).executeUpdate();

            transaction.commit();
            return true;
        } catch (GenericJDBCException | SQLGrammarException e) {
            session.getTransaction().rollback();
            throw e;
        } finally {
            session.close();
        }

    }


    // Transaction is useless is this case.


}
