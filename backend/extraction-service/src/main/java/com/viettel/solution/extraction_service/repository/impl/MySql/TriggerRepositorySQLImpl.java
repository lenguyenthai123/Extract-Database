package com.viettel.solution.extraction_service.repository.impl.MySql;

import com.viettel.solution.extraction_service.database.DatabaseConnection;
import com.viettel.solution.extraction_service.entity.Trigger;
import com.viettel.solution.extraction_service.repository.TriggerRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.NativeQuery;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
@Qualifier("triggerRepositorySQLImpl")
public class TriggerRepositorySQLImpl implements TriggerRepository {

    @Override
    public Trigger getTrigger(SessionFactory sessionFactory, String databaseName, String schemaName, String tableName, String triggerName) {
        Trigger trigger = null;

        try (Session session = sessionFactory.openSession()) {
            String query = "SELECT TRIGGER_NAME as name, EVENT_MANIPULATION as event, "
                    + "EVENT_OBJECT_TABLE as tableName, ACTION_TIMING as timing, "
                    + "ACTION_CONDITION as actionCondition, ACTION_STATEMENT as doAction "
                    + "FROM INFORMATION_SCHEMA.TRIGGERS "
                    + "WHERE TRIGGER_SCHEMA = :schemaName AND EVENT_OBJECT_TABLE = :tableName AND TRIGGER_NAME = :triggerName";

            NativeQuery<Trigger> nativeQuery = session.createNativeQuery(query);
            nativeQuery.setParameter("schemaName", schemaName);
            nativeQuery.setParameter("tableName", tableName);
            nativeQuery.setParameter("triggerName", triggerName);
            nativeQuery.setResultTransformer(Transformers.aliasToBean(Trigger.class));

            trigger = (Trigger) nativeQuery.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return trigger;
    }


    @Override
    public List<Trigger> getAllTrigger(SessionFactory sessionFactory, String databaseName, String schemaName, String tableName) {
        List<Trigger> triggers = null;

        try (Session session = sessionFactory.openSession()) {
            String query = "SELECT TRIGGER_NAME as name, EVENT_MANIPULATION as event, "
                    + "EVENT_OBJECT_TABLE as tableName, ACTION_TIMING as timing, "
                    + "ACTION_CONDITION as actionCondition, ACTION_STATEMENT as doAction "
                    + "FROM INFORMATION_SCHEMA.TRIGGERS "
                    + "WHERE TRIGGER_SCHEMA = :schemaName AND EVENT_OBJECT_TABLE = :tableName";

            NativeQuery<Trigger> nativeQuery = session.createNativeQuery(query);
            nativeQuery.setParameter("schemaName", schemaName);
            nativeQuery.setParameter("tableName", tableName);
            nativeQuery.setResultTransformer(Transformers.aliasToBean(Trigger.class));

            triggers = nativeQuery.list();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return triggers;
    }
}
