package com.viettel.solution.extraction_service.repository.impl.MySql;

import com.viettel.solution.extraction_service.database.DatabaseConnection;
import com.viettel.solution.extraction_service.entity.*;
import com.viettel.solution.extraction_service.repository.ConstraintRepository;
import com.viettel.solution.extraction_service.repository.IndexRepository;
import com.viettel.solution.extraction_service.repository.TableRepository;
import com.viettel.solution.extraction_service.repository.TriggerRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.NativeQuery;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

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
            tableEntity.setSchemaName(databaseName);
            // Lấy toàn bộ các constraint trong bảng.
            List<Constraint> constraints = constraintRepository.getAllConstraintFromTable(sessionFactory, databaseName, schemaName, tableName);
            if (constraints != null) {
                tableEntity.setConstraints(constraints);
            }

            // Lấy toàn bộ các index trong bảng.
            List<Index> indexs = indexRepository.getAllIndexFromTable(sessionFactory, databaseName, schemaName, tableName);
            if (indexs != null) {
                tableEntity.setIndexs(indexs);
            }

            // Lấy trigger liên quan đến bảng này
            List<Trigger> triggers = triggerRepository.getTriggerListFromTable(sessionFactory, databaseName, schemaName, tableName);
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
        try (Session session = sessionFactory.openSession()) {
            String sql = "select TABLE_NAME as name, TABLE_COMMENT as description " +
                    "from information_schema.tables " +
                    "where TABLE_SCHEMA=:schemaName";

            NativeQuery<Table> nativeQuery = session.createNativeQuery(sql, Table.class);
            nativeQuery.setParameter("schemaName", databaseName);
            nativeQuery.setResultTransformer(Transformers.aliasToBean(Table.class));

            List<Table> tables = nativeQuery.getResultList();
            List<Table> result = new ArrayList<>();
            for (Table table : tables) {
                result.add(getTable(sessionFactory, databaseName, schemaName, table.getName()));
            }

            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Table> getAllTableFromDatabase(SessionFactory sessionFactory) {
        try {
            DatabaseMetaData metaData = DatabaseConnection.getDatabaseMetaData(sessionFactory);

            ResultSet schemaSet = metaData.getCatalogs();
            List<String> schemas = new ArrayList<>();

            while (schemaSet.next()) {
                String databaseName = schemaSet.getString(1);
                schemas.add(databaseName);
            }

            List<Table> tables = new ArrayList<>();
            if (metaData == null) {
                return null;
            }

            for (String schemaName : schemas) {

                tables.addAll(getAllTable(sessionFactory, schemaName, null));
            }
            return tables;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }


    @Override
    public List<Table> getAllTableName(SessionFactory sessionFactory, String databaseName, String schemaName) {
        try (Session session = sessionFactory.openSession()) {
            String sql = "select TABLE_NAME as name, TABLE_COMMENT as description " +
                    "from information_schema.tables " +
                    "where TABLE_SCHEMA=:schemaName";

            NativeQuery<Table> nativeQuery = session.createNativeQuery(sql, Table.class);
            nativeQuery.setParameter("schemaName", schemaName);
            nativeQuery.setResultTransformer(Transformers.aliasToBean(Table.class));

            List<Table> tables = nativeQuery.getResultList();

            return tables;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
