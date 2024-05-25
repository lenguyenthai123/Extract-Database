package com.viettel.solution.extraction_service.repository.impl;

import com.viettel.solution.extraction_service.entity.Index;
import com.viettel.solution.extraction_service.repository.IndexRepository;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


@Repository
public class IndexRepositoryImpl implements IndexRepository {

    @Override
    public Index getIndex(Connection connection, String databaseName, String schemaName, String tableName, String indexName) {
        Index index = null;

        try {
            DatabaseMetaData metaData = connection.getMetaData();
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
    }

    @Override
    public List<Index> getAllIndex(Connection connection, String databaseName, String schemaName, String tableName) {
        List<Index> indexes = new ArrayList<>();

        try {
            DatabaseMetaData metaData = connection.getMetaData();
            try (ResultSet rs = metaData.getIndexInfo(databaseName, schemaName, tableName, false, false)) {
                while (rs.next()) {
                    String currentIndexName = rs.getString("INDEX_NAME");
                    if (currentIndexName != null) {
                        Index index = findIndexByName(indexes, currentIndexName);
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

    private Index findIndexByName(List<Index> indexes, String indexName) {
        for (Index index : indexes) {
            if (index.getName().equalsIgnoreCase(indexName)) {
                return index;
            }
        }
        return null;
    }
}
