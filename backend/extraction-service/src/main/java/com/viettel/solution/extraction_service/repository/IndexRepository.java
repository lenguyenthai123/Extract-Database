package com.viettel.solution.extraction_service.repository;

import com.viettel.solution.extraction_service.entity.Constraint;
import com.viettel.solution.extraction_service.entity.Index;
import org.hibernate.SessionFactory;

import java.sql.Connection;
import java.util.List;

public interface IndexRepository {

    public Index getIndex(SessionFactory sessionFactory, String databaseName, String schemaName, String tableName, String indexName);

    public List<Index> getAllIndex(SessionFactory sessionFactory, String databaseName, String schemaName, String tableName);
}
