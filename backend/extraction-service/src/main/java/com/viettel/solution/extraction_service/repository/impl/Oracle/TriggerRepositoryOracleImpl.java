package com.viettel.solution.extraction_service.repository.impl.Oracle;

import com.viettel.solution.extraction_service.entity.Trigger;
import com.viettel.solution.extraction_service.repository.TriggerRepository;
import com.viettel.solution.extraction_service.utils.CustomResultTransformer;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.NativeQuery;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Qualifier("triggerRepositoryOracleImpl")
public class TriggerRepositoryOracleImpl implements TriggerRepository {

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
            nativeQuery.setParameter("schemaName", databaseName);
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
    public List<Trigger> getTriggerListFromTable(SessionFactory sessionFactory, String databaseName, String schemaName, String tableName) {
        List<Trigger> triggers = null;

        try (Session session = sessionFactory.openSession()) {

            String query = "SELECT TRIGGER_NAME as name, "
                    + "TRIGGERING_EVENT as event, "
                    + "TABLE_NAME as table_name, "
                    + "TRIGGER_TYPE as timing, "
                    + "TRIGGER_BODY as do_action "
                    + "FROM ALL_TRIGGERS "
                    + "WHERE OWNER = :schemaName AND TABLE_NAME = :tableName";

            NativeQuery nativeQuery = session.createNativeQuery(query);
            nativeQuery.setParameter("schemaName", schemaName.toUpperCase());
            nativeQuery.setParameter("tableName", tableName.toUpperCase());
            nativeQuery.setResultTransformer(new CustomResultTransformer(Trigger.class));
            triggers = nativeQuery.list();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return triggers;
    }

    @Override
    public Trigger save(SessionFactory sessionFactory, Trigger trigger) {
        return null;
    }


    @Override
    public boolean delete(SessionFactory sessionFactory, String schemaName, String tableName, String triggerName) {
        return false;
    }

    @Override
    public Trigger update(SessionFactory sessionFactory, Trigger trigger) {
        return null;
    }


}
