package com.viettel.solution.extraction_service.repository;

import com.viettel.solution.extraction_service.entity.Table;
import org.hibernate.SessionFactory;

import java.sql.Connection;
import java.util.List;

public interface TableRepository {


    public Table getTable(SessionFactory sessionFactory, String databaseName, String schemaName, String tableName);

    public List<Table> getAllTable(SessionFactory sessionFactory, String databaseName, String schemaName);

    public List<Table> getAllTableName(SessionFactory sessionFactory, String databaseName, String schemaName);

}
