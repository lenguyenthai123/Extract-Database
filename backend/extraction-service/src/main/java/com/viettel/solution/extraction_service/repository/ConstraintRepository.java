package com.viettel.solution.extraction_service.repository;

import com.viettel.solution.extraction_service.entity.Constraint;

import java.sql.Connection;
import java.util.List;

public interface ConstraintRepository {
    public Constraint getConstraint(Connection connection, String databaseName, String schemaName, String tableName, String constraintName);

    public List<Constraint> getAllConstraint(Connection connection, String databaseName, String schemaName, String tableName);
}
