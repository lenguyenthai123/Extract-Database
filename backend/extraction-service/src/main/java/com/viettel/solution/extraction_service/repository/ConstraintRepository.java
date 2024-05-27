package com.viettel.solution.extraction_service.repository;

import com.viettel.solution.extraction_service.entity.Constraint;
import org.hibernate.SessionFactory;

import java.sql.Connection;
import java.util.List;

public interface ConstraintRepository {
    public Constraint getConstraint(SessionFactory sessionFactory, String databaseName, String schemaName, String tableName, String constraintName);

    public List<Constraint> getAllConstraint(SessionFactory sessionFactory, String databaseName, String schemaName, String tableName);
}
