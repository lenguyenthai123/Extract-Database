package com.viettel.solution.extraction_service.repository;

import com.viettel.solution.extraction_service.dto.IndexDto;
import com.viettel.solution.extraction_service.entity.Constraint;
import com.viettel.solution.extraction_service.entity.Index;
import com.viettel.solution.extraction_service.entity.Trigger;
import org.hibernate.SessionFactory;

import java.sql.Connection;
import java.util.List;

public interface IndexRepository {

    public Index get(SessionFactory sessionFactory, String schemaName, String tableName, String indexName);

    public List<Index> getAllIndexFromTable(SessionFactory sessionFactory, String databaseName, String schemaName, String tableName);

    public boolean save(SessionFactory sessionFactory, Index index);

    public boolean delete(SessionFactory sessionFactory, Index index);

    public boolean update(SessionFactory sessionFactory, Index index, String oldIndexName);
}
