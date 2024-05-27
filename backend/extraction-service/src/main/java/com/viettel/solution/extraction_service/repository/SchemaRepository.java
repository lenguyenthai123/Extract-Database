package com.viettel.solution.extraction_service.repository;

import com.viettel.solution.extraction_service.entity.Schema;
import org.hibernate.SessionFactory;

import java.sql.Connection;
import java.util.List;

public interface SchemaRepository {

    //----------------------------------------------------------------------
    public Schema getSchema(SessionFactory sessionFactory, String databaseName, String schemaName);

    public List<Schema> getAllSchema(SessionFactory sessionFactory, String databaseName);

    public List<String> getAllSchemaName(SessionFactory sessionFactory);

}
