package com.viettel.solution.extraction_service.dao;

import com.viettel.solution.extraction_service.entity.Trigger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.NativeQuery;

import java.util.List;

public interface TriggerDao {


    public void save(SessionFactory sessionFactory, Trigger trigger);

    public Trigger findBySchemaTableTriggerName(SessionFactory sessionFactory, String schemaName, String tableName, String triggerName);

    public List<Trigger> findBySchemaTableName(SessionFactory sessionFactory, String schemaName, String tableName);

}
