package com.viettel.solution.extraction_service.repository.impl.MySql;

import com.viettel.solution.extraction_service.database.DatabaseConnection;
import com.viettel.solution.extraction_service.entity.Trigger;
import com.viettel.solution.extraction_service.repository.TriggerRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.exception.GenericJDBCException;
import org.hibernate.exception.SQLGrammarException;
import org.hibernate.query.NativeQuery;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.hibernate.query.Query;

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
                    + "ACTION_CONDITION as actionCondition, ACTION_STATEMENT as doAction,"
                    + "TRIGGER_SCHEMA as schemaName "
                    + "FROM INFORMATION_SCHEMA.TRIGGERS "
                    + "WHERE TRIGGER_SCHEMA = :schemaName AND EVENT_OBJECT_TABLE = :tableName AND TRIGGER_NAME = :triggerName";

            NativeQuery<Trigger> nativeQuery = session.createNativeQuery(query);
            nativeQuery.setParameter("schemaName", schemaName);
            nativeQuery.setParameter("tableName", tableName);
            nativeQuery.setParameter("triggerName", triggerName);
            nativeQuery.setResultTransformer(Transformers.aliasToBean(Trigger.class));

            trigger = (Trigger) nativeQuery.uniqueResult();
        } catch (GenericJDBCException | SQLGrammarException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return trigger;
    }


    @Override
    public List<Trigger> getTriggerListFromTable(SessionFactory sessionFactory, String databaseName, String schemaName, String tableName) {
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
        } catch (GenericJDBCException | SQLGrammarException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return triggers;
    }

    @Override
    public boolean save(SessionFactory sessionFactory, Trigger trigger) {
        boolean success = false;
        Session session = sessionFactory.openSession();
        try {
            //Open transaction

            String schemaName = trigger.getSchemaName();
            String triggerName = trigger.getName();
            String tableName = trigger.getTableName();
            String event = trigger.getEvent();
            String timing = trigger.getTiming();
            String actionStatement = trigger.getDoAction();

            // Tạo câu lệnh CREATE TRIGGER
            String createQuery = String.format(
                    "CREATE TRIGGER %s.%s %s %s ON %s.%s FOR EACH ROW %s",
                    schemaName, triggerName, timing, event, schemaName, tableName, actionStatement
            );

            NativeQuery<?> createNativeQuery = session.createNativeQuery(createQuery);
            session.beginTransaction();
            createNativeQuery.executeUpdate();

            session.getTransaction().commit();
            success = true;
            return success;

        } catch (GenericJDBCException | SQLGrammarException e) {
            session.getTransaction().rollback();
            throw e;
        } catch (Exception e) {
            session.getTransaction().rollback();
            System.out.println(e.getLocalizedMessage());
            System.out.println(e.getMessage());

            return success;

        } finally {
            try {
                session.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    // Phải xóa trigger cũ và tạo trigger mới
    // https://stackoverflow.com/questions/11525930/can-you-modify-an-existing-mysql-trigger-after-it-has-been-created
    //    //https://dev.mysql.com/doc/refman/8.4/en/cannot-roll-back.html#:~:text=Some%20statements%20cannot%20be%20rolled,alter%20tables%20or%20stored%20routines.
    // Can not rollback with DDL so we have to take the backup.
    // => Transaction is useless!!!!!!!!! So painfull and lots of time to found out
    @Override
    public boolean update(SessionFactory sessionFactory, Trigger trigger) {
        boolean success = false;
        Trigger triggerBackUp = null;
        Session session = sessionFactory.openSession();
        boolean afterDelete = false;
        boolean afterAdd = false;
        try {
            //Open transaction
            session.beginTransaction();

            String schemaName = trigger.getSchemaName();
            String oldName = trigger.getOldName();
            String triggerName = trigger.getName();
            String tableName = trigger.getTableName();
            String event = trigger.getEvent();
            String timing = trigger.getTiming();
            String actionStatement = trigger.getDoAction();

            // Lưu lại backup trước khi xóa
            triggerBackUp = this.getTrigger(sessionFactory, null, schemaName, tableName, oldName);

            // Xóa trigger cũ

            String dropQuery = String.format("DROP TRIGGER IF EXISTS %s.%s", schemaName, oldName);
            NativeQuery<?> dropNativeQuery = session.createNativeQuery(dropQuery);
            dropNativeQuery.executeUpdate();

            // Gán cờ đánh dấu là đã XÓA THÀNH CÔNG.
            afterDelete = true;


            System.out.println("Transaction is live: " + session.getTransaction().isActive());
            // Tạo trigger mới với các thuộc tính cập nhật
            String createQuery = String.format(
                    "CREATE TRIGGER %s.%s %s %s ON %s.%s FOR EACH ROW %s",
                    schemaName, triggerName, timing, event, schemaName, tableName, actionStatement
            );
            NativeQuery<?> createNativeQuery = session.createNativeQuery(createQuery);
            createNativeQuery.executeUpdate();

            // Gán cờ đánh dấu là đã XÓA THÀNH CÔNG.
            afterAdd = true;

            session.getTransaction().commit();
            success = true;
            return success;

        } catch (GenericJDBCException | SQLGrammarException e) {
            if (afterDelete && !afterAdd) {
                boolean flag = save(sessionFactory, triggerBackUp);
                if (flag) System.out.println("Make backup successfull");
                else System.out.println("Make backup successfully!");
            }
            throw e;
        } catch (Exception e) {
            session.getTransaction().rollback();

            // This is a real ROLLBACK.
            if (afterDelete && !afterAdd) {
                boolean flag = save(sessionFactory, triggerBackUp);
                if (flag) System.out.println("Make backup successfull");
                else System.out.println("Make backup successfully!");
            }

            e.printStackTrace();
            return false;
        } finally {
            session.close();
        }
    }


    @Override
    public boolean delete(SessionFactory sessionFactory, String schemaName, String tableName, String triggerName) {
        boolean success = false;
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            // Xóa trigger cũ
            String dropQuery = String.format("DROP TRIGGER IF EXISTS %s.%s", schemaName, triggerName);
            NativeQuery<?> dropNativeQuery = session.createNativeQuery(dropQuery);
            dropNativeQuery.executeUpdate();

            transaction.commit();
            success = true;
        } catch (GenericJDBCException | SQLGrammarException e) {
            transaction.rollback();

            throw e;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
        return success;
    }
}
