package com.viettel.solution.extraction_service.repository.impl.MySql;

import com.viettel.solution.extraction_service.entity.Index;
import com.viettel.solution.extraction_service.entity.Trigger;
import com.viettel.solution.extraction_service.repository.IndexRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.exception.GenericJDBCException;
import org.hibernate.exception.SQLGrammarException;
import org.hibernate.query.NativeQuery;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@Qualifier("Mysql")
public class IndexRepositoryMySQLImpl implements IndexRepository {
    @Override
    public Index get(SessionFactory sessionFactory, String schemaName, String tableName, String indexName) {
        Index index = null;
        List<Object[]> rows = null;

        try (Session session = sessionFactory.openSession()) {
            String query = "SELECT INDEX_NAME as name, TABLE_NAME as tableName, INDEX_SCHEMA as schemaName, COLUMN_NAME as columnName "
                    + "FROM INFORMATION_SCHEMA.STATISTICS "
                    + "WHERE INDEX_SCHEMA = :schemaName AND TABLE_NAME = :tableName AND INDEX_NAME = :indexName";

            NativeQuery<Object[]> nativeQuery = session.createNativeQuery(query, Object[].class);
            nativeQuery.setParameter("schemaName", schemaName);
            nativeQuery.setParameter("tableName", tableName);
            nativeQuery.setParameter("indexName", indexName);

            rows = nativeQuery.list();
            index = new Index();

            boolean first = true;

            for (Object[] row : rows) {
                if (first) {
                    index.setName(row[0] != null ? row[0].toString() : null);
                    index.setTableName(row[1] != null ? row[1].toString() : null);
                    index.setSchemaName(row[2] != null ? row[2].toString() : null);
                    first = false;
                }
                index.getColumns().add(row[3] != null ? row[3].toString() : null);
            }
            return index;

        } catch (GenericJDBCException | SQLGrammarException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public List<Index> getAllIndexFromTable(SessionFactory sessionFactory, String databaseName, String schemaName, String tableName) {
        List<Index> indexs = new ArrayList<>();
        List<Object[]> rows = null;

        try (Session session = sessionFactory.openSession()) {
            String query = "SELECT INDEX_NAME as name, TABLE_NAME as tableName, INDEX_SCHEMA as schemaName, COLUMN_NAME as columnName "
                    + "FROM INFORMATION_SCHEMA.STATISTICS "
                    + "WHERE INDEX_SCHEMA = :schemaName AND TABLE_NAME = :tableName";

            NativeQuery<Object[]> nativeQuery = session.createNativeQuery(query, Object[].class);
            nativeQuery.setParameter("schemaName", schemaName);
            nativeQuery.setParameter("tableName", tableName);
            rows = nativeQuery.list();
            for (Object[] row : rows) {
                Index index1 = new Index();
                index1.setName(row[0] != null ? row[0].toString() : null);
                index1.setTableName(row[1] != null ? row[1].toString() : null);
                index1.setSchemaName(row[2] != null ? row[2].toString() : null);
                index1.getColumns().add(row[3] != null ? row[3].toString() : null);
                indexs.add(index1);
            }
            return indexs;

        } catch (GenericJDBCException | SQLGrammarException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public boolean save(SessionFactory sessionFactory, Index index) {
        boolean success = false;
        Session session = null;
        try {
            session = sessionFactory.openSession();
            //Open transaction
            session.beginTransaction();
            String schemaName = index.getSchemaName();
            String triggerName = index.getName();
            String tableName = index.getTableName();
            String name = index.getName();
            String[] columnList = index.getColumns().toArray(new String[0]);

            String columns = "";

            if (columnList.length == 1) {
                columns = columnList[0];
            } else {
                for (int i = 0; i < columnList.length; i++) {
                    columns += columnList[i];
                    if (i < columnList.length - 1) {
                        columns += ", ";
                    }
                }
            }
            System.out.println(columns);

            // Tạo câu lệnh CREATE TRIGGER
            String createQuery = String.format(
                    "CREATE INDEX %s ON %s.%s (%s)",
                    triggerName, schemaName, tableName, columns
            );

            NativeQuery<?> createNativeQuery = session.createNativeQuery(createQuery);

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
                if (session != null) {
                    session.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public boolean delete(SessionFactory sessionFactory, String schemaName, String tableName, String indexName) {
        boolean success = false;
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            // Xóa trigger cũ
            String dropQuery = String.format("ALTER TABLE %s.%s DROP INDEX %s", schemaName, tableName, indexName);
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

    @Override
    public boolean update(SessionFactory sessionFactory, Index index, String oldIndexName) {
        boolean afterDelete = false;
        boolean afterAdd = false;
        Index oldIndex = null;
        try {

            String schemaName = index.getSchemaName();
            String tableName = index.getTableName();
            String indexName = index.getName();
            String oldIndexname = oldIndexName;

            // Lấy lại index cu
            oldIndex = get(sessionFactory, schemaName, tableName, oldIndexname);

            // Xóa index cũ
            delete(sessionFactory, schemaName, tableName, oldIndexname);
            afterDelete = true;

            // Tạo index mới
            save(sessionFactory, index);
            afterAdd = true;


            return true;
        } catch (GenericJDBCException | SQLGrammarException e) {
            if (afterDelete && !afterAdd) {
                boolean flag = save(sessionFactory, oldIndex);
                if (flag) System.out.println("Make backup successfull");
                else System.out.println("Make backup successfully!");
            }

            throw e;
        } catch (Exception e) {
            if (afterDelete && !afterAdd) {
                boolean flag = save(sessionFactory, oldIndex);
                if (flag) System.out.println("Make backup successfull");
                else System.out.println("Make backup successfully!");
            }

            e.printStackTrace();
        }
        return false;
    }
}


