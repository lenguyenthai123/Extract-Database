package com.viettel.solution.extraction_service.repository;

import com.viettel.solution.extraction_service.entity.Constraint;
import com.viettel.solution.extraction_service.entity.Index;
import org.hibernate.SessionFactory;

import java.sql.Connection;
import java.util.List;

public interface ConstraintRepository {
    public Constraint getConstraint(SessionFactory sessionFactory, String databaseName, String schemaName, String tableName, String constraintName);

    public List<Constraint> getAllConstraintFromTable(SessionFactory sessionFactory, String databaseName, String schemaName, String tableName);

    public Constraint save(SessionFactory sessionFactory, Constraint constraint);

    public boolean delete(SessionFactory sessionFactory, Constraint constraint);

    public Constraint update(SessionFactory sessionFactory, Constraint constraint, String oldConstraintxName);
}
