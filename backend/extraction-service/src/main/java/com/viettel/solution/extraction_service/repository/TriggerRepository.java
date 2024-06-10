package com.viettel.solution.extraction_service.repository;

import com.viettel.solution.extraction_service.entity.Constraint;
import com.viettel.solution.extraction_service.entity.Table;
import com.viettel.solution.extraction_service.entity.Trigger;
import org.hibernate.SessionFactory;

import java.sql.Connection;
import java.util.List;

public interface TriggerRepository {

    public Trigger getTrigger(SessionFactory sessionFactory, String databaseName, String schemaName, String tableName, String triggerName);

    public List<Trigger> getTriggerListFromTable(SessionFactory sessionFactory, String databaseName, String schemaName, String tableName);

    public Trigger save(SessionFactory sessionFactory, Trigger trigger);

    public boolean delete(SessionFactory sessionFactory, String schemaName, String tableName, String triggerName);

    public Trigger update(SessionFactory sessionFactory, Trigger trigger);
}
