package com.viettel.solution.extraction_service.dao.impl;

import com.viettel.solution.extraction_service.dao.TriggerDao;
import com.viettel.solution.extraction_service.entity.Trigger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.NativeQuery;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TriggerDaoImpl implements TriggerDao {

    public void save(SessionFactory sessionFactory, Trigger trigger) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            session.persist(trigger);
            session.getTransaction().commit();
            System.out.println("insert trigger success!");
        } catch (RuntimeException e) {
            session.getTransaction().rollback();
            e.printStackTrace();
            throw e;
        } finally {
            session.flush();
            session.close();
        }
    }

    public Trigger findBySchemaTableTriggerName(SessionFactory sessionFactory, String schemaName, String tableName, String triggerName) {
        Session session = sessionFactory.openSession();
        String sql = "SELECT * FROM information_schema.triggers" +
                " WHERE trigger_schema = :schemaName AND " +
                "event_object_table = :tableName AND " +
                "trigger_name = :triggerName";
        NativeQuery nativeQuery = session.createNativeQuery(sql)
                .setParameter("schemaName", schemaName)
                .setParameter("tableName", tableName)
                .setParameter("triggerName", triggerName);

        Trigger trigger = (Trigger) nativeQuery.uniqueResult();

        return trigger;
    }

    public List<Trigger> findBySchemaTableName(SessionFactory sessionFactory, String schemaName, String tableName) {
        Session session = sessionFactory.openSession();
        try {
            String sql = "SELECT * FROM information_schema.triggers" +
                    " WHERE trigger_schema = :schemaName AND " +
                    "event_object_table = :tableName";
            NativeQuery<Trigger> nativeQuery = session.createNativeQuery(sql)
                    .setParameter("schemaName", schemaName)
                    .setParameter("tableName", tableName);

            List<Trigger> triggers = nativeQuery.list();

            return triggers;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            session.close();
        }

    }
}
