package com.viettel.solution.extraction_service.repository.impl;

import com.viettel.solution.extraction_service.database.DatabaseConnection;
import com.viettel.solution.extraction_service.entity.Index;
import com.viettel.solution.extraction_service.repository.IndexRepository;
import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


@Repository
@Primary
public class IndexRepositoryImpl implements IndexRepository {

/*    @Override
    public Index getAllIndexFromTable(SessionFactory sessionFactory, String databaseName, String schemaName, String tableName, String indexName) {
        Index index = null;

        try {
            DatabaseMetaData metaData = DatabaseConnection.getDatabaseMetaData(sessionFactory);
            if (metaData == null) {
                return null;
            }

            try (ResultSet rs = metaData.getIndexInfo(databaseName, schemaName, tableName, false, false)) {
                while (rs.next()) {
                    String currentIndexName = rs.getString("INDEX_NAME");
                    if (currentIndexName != null && currentIndexName.equalsIgnoreCase(indexName)) {
                        if (index == null) {
                            index = new Index();
                            index.setName(currentIndexName);
                            index.setTableName(rs.getString("TABLE_NAME"));
                        }
                        index.getColumns().add(rs.getString("COLUMN_NAME"));
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return index;
    }*/

    @Override
    public Index get(SessionFactory sessionFactory, String schemaName, String tableName, String indexName) {
        return null;
    }

    @Override
    public List<Index> getAllIndexFromTable(SessionFactory sessionFactory, String databaseName, String schemaName, String tableName) {
        List<Index> indexes = new ArrayList<>();

        try {
            DatabaseMetaData metaData = DatabaseConnection.getDatabaseMetaData(sessionFactory);
            try (ResultSet rs = metaData.getIndexInfo(databaseName, schemaName, tableName, false, false)) {
                while (rs.next()) {
                    String currentIndexName = rs.getString("INDEX_NAME");
                    if (currentIndexName != null) {
                        Index index = null;

                        // Find index by name if it exists before so we can add columns to it
                        // => index with mutiple columns
                        for (Index i : indexes) {
                            if (i.getName().equalsIgnoreCase(currentIndexName)) {
                                index = i;
                                break;
                            }
                        }

                        if (index == null) {
                            index = new Index();
                            index.setName(currentIndexName);
                            index.setTableName(rs.getString("TABLE_NAME"));
                            indexes.add(index);
                        }
                        index.getColumns().add(rs.getString("COLUMN_NAME"));
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return indexes;
    }

    @Override
    public boolean save(SessionFactory sessionFactory, Index index) {
        return false;
    }

    @Override
    public boolean delete(SessionFactory sessionFactory, String schemaName, String tableName, String indexName) {
        return false;
    }

    @Override
    public boolean update(SessionFactory sessionFactory, Index index, String oldIndexName) {
        return false;
    }


    private Index findIndexByName(List<Index> indexes, String indexName) {
        for (Index index : indexes) {
            if (index.getName().equalsIgnoreCase(indexName)) {
                return index;
            }
        }
        return null;
    }
}
