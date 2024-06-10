package com.viettel.solution.extraction_service.repository.impl.MySql;

import com.viettel.solution.extraction_service.entity.Constraint;
import com.viettel.solution.extraction_service.entity.Index;
import com.viettel.solution.extraction_service.entity.Trigger;
import com.viettel.solution.extraction_service.repository.ConstraintRepository;
import com.viettel.solution.extraction_service.utils.CustomResultTransformer;
import org.apache.poi.ss.formula.functions.T;
import org.checkerframework.checker.units.qual.C;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.exception.GenericJDBCException;
import org.hibernate.exception.SQLGrammarException;
import org.hibernate.query.NativeQuery;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Repository
@Qualifier("Mysql")
public class ConstraintRepositoryMySQLImpl implements ConstraintRepository {


    @Override
    public Constraint getConstraint(SessionFactory sessionFactory, String databaseName, String schemaName, String tableName, String constraintName) {
        List<Constraint> constraints = null;

        try (Session session = sessionFactory.openSession()) {
            String query = "SELECT C.CONSTRAINT_SCHEMA AS schema_name, " + "C.TABLE_NAME AS table_name, " + "C.CONSTRAINT_NAME AS name, " + "T.CONSTRAINT_TYPE AS constraint_type, " + "C.COLUMN_NAME AS column_name, " + "C.REFERENCED_TABLE_NAME AS ref_table_name, " + "C.REFERENCED_COLUMN_NAME AS ref_column_name " + "FROM information_schema.KEY_COLUMN_USAGE AS C " + "JOIN information_schema.TABLE_CONSTRAINTS AS T " + "ON C.CONSTRAINT_SCHEMA = T.CONSTRAINT_SCHEMA " + "AND C.TABLE_NAME = T.TABLE_NAME " + "AND C.CONSTRAINT_NAME = T.CONSTRAINT_NAME " + "WHERE C.CONSTRAINT_SCHEMA = :schemaName " + "AND C.TABLE_NAME = :tableName " + "AND C.CONSTRAINT_NAME = :constraintName;";

            NativeQuery<Constraint> nativeQuery = session.createNativeQuery(query, Constraint.class);
            nativeQuery.setParameter("schemaName", schemaName);
            nativeQuery.setParameter("tableName", tableName);
            nativeQuery.setParameter("constraintName", constraintName);
            nativeQuery.setResultTransformer(new CustomResultTransformer(Constraint.class));

            constraints = nativeQuery.getResultList();

            Constraint result = null;

            boolean first = true;
            for (Constraint constraint : constraints) {
                if (first) {
                    result = new Constraint(constraint);
                    first = false;
                }
                result.getColumnList().add(constraint.getColumnName());
            }

            return result;

        } catch (GenericJDBCException | SQLGrammarException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public List<Constraint> getAllConstraintFromTable(SessionFactory sessionFactory, String databaseName, String schemaName, String tableName) {
        List<Constraint> constraints = new ArrayList<>();
        List<Constraint> rows = null;

        try (Session session = sessionFactory.openSession()) {
            String query = "SELECT C.CONSTRAINT_SCHEMA AS schema_name, " + "C.TABLE_NAME AS table_name, " + "C.CONSTRAINT_NAME AS name, " + "T.CONSTRAINT_TYPE AS constraint_type, " + "C.COLUMN_NAME AS column_name, " + "C.REFERENCED_TABLE_NAME AS ref_table_name, " + "C.REFERENCED_COLUMN_NAME AS ref_column_name " + "FROM information_schema.KEY_COLUMN_USAGE AS C " + "JOIN information_schema.TABLE_CONSTRAINTS AS T " + "ON C.CONSTRAINT_SCHEMA = T.CONSTRAINT_SCHEMA " + "AND C.TABLE_NAME = T.TABLE_NAME " + "AND C.CONSTRAINT_NAME = T.CONSTRAINT_NAME " + "WHERE C.CONSTRAINT_SCHEMA = :schemaName " + "AND C.TABLE_NAME = :tableName;";

            NativeQuery<Constraint> nativeQuery = session.createNativeQuery(query, Constraint.class);
            nativeQuery.setParameter("schemaName", schemaName);
            nativeQuery.setParameter("tableName", tableName);
            nativeQuery.setResultTransformer(new CustomResultTransformer(Constraint.class));

            constraints = nativeQuery.getResultList();

            for (Constraint constraint : constraints) {
                constraint.getColumnList().add(constraint.getColumnName());
            }

            return constraints;

        } catch (GenericJDBCException | SQLGrammarException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public Constraint save(SessionFactory sessionFactory, Constraint constraint) {
        boolean success = false;
        Session session = null;
        try {
            session = sessionFactory.openSession();
            //Open transaction
            session.beginTransaction();
            String schemaName = constraint.getSchemaName();
            String tableName = constraint.getTableName();
            String name = constraint.getName();
            String refTableName = constraint.getRefTableName();
            String refColumnName = constraint.getRefColumnName();
            String constraintType = constraint.getConstraintType();
            String expression = constraint.getExpression();
            String[] columnList = constraint.getColumnList().toArray(new String[0]);


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


            String createQuery = "";

            boolean check = true;

            switch (constraintType) {
                case "PRIMARY KEY":
                    // Tạo câu lệnh CREATE PRIMARY KEY
                    createQuery = String.format("ALTER TABLE %s.%s ADD CONSTRAINT %s PRIMARY KEY (%s)", schemaName, tableName, name, columns);
                    break;
                case "FOREIGN KEY":
                    // Tạo câu lệnh CREATE FOREIGN KEY
                    createQuery = String.format("ALTER TABLE %s.%s ADD CONSTRAINT %s FOREIGN KEY (%s) REFERENCES %s.%s(%s)", schemaName, tableName, name, columns, schemaName, refTableName, refColumnName);
                    break;
                case "UNIQUE":
                    // Tạo câu lệnh CREATE UNIQUE
                    createQuery = String.format("ALTER TABLE %s.%s ADD CONSTRAINT %s UNIQUE (%s)", schemaName, tableName, name, columns);
                    break;
                case "CHECK":
                    // Tạo câu lệnh CREATE CHECK
                    createQuery = String.format("ALTER TABLE %s.%s ADD CONSTRAINT %s CHECK (%s)", schemaName, tableName, name, expression);
                    break;
                default:
                    check = false;
                    break;
            }
            if (!check) {
                throw new RuntimeException("Constraint type is not valid");
            }

            NativeQuery<?> createNativeQuery = session.createNativeQuery(createQuery);

            createNativeQuery.executeUpdate();

            session.getTransaction().commit();
            success = true;
            return constraint;

        } catch (GenericJDBCException | SQLGrammarException e) {
            session.getTransaction().rollback();
            throw e;
        } catch (Exception e) {
            session.getTransaction().rollback();
            System.out.println(e.getLocalizedMessage());
            System.out.println(e.getMessage());

            return null;

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
    public boolean delete(SessionFactory sessionFactory, Constraint constraint) {
        boolean success = false;
        Session session = null;

        boolean afterDelete = false;
        boolean afterAdd = false;

        try {
            session = sessionFactory.openSession();
            //Open transaction
            session.beginTransaction();

            String schemaName = constraint.getSchemaName();
            String tableName = constraint.getTableName();
            String constraintName = constraint.getName();
            String constraintType = constraint.getConstraintType();


            String createQuery = "";
            String updateQuery = "";

            NativeQuery<?> createNativeQuery;
            NativeQuery<?> updateNativeQuery;

            boolean check = true;

            switch (constraintType) {
                case "PRIMARY KEY":
                    // Tao backup

                    String[] columnList = constraint.getColumnList().toArray(new String[0]);


                    Constraint backupConstraint = getConstraint(sessionFactory, null, schemaName, tableName, constraintName);
                    List<String> oldColumnList = backupConstraint.getColumnList();

                    for (String column : columnList) {
                        oldColumnList.remove(column);
                    }

                    String columns = "";
                    if (!oldColumnList.isEmpty()) {
                        if (oldColumnList.size() == 1) {
                            columns = oldColumnList.get(0);
                        } else {
                            for (int i = 0; i < oldColumnList.size(); i++) {
                                columns += oldColumnList.get(i);
                                if (i < oldColumnList.size() - 1) {
                                    columns += ", ";
                                }
                            }
                        }
                        updateQuery = String.format("ALTER TABLE %s.%s DROP PRIMARY KEY, ADD PRIMARY KEY(%s)", schemaName, tableName, columns);

                        updateNativeQuery = session.createNativeQuery(updateQuery);
                        updateNativeQuery.executeUpdate();

                    } else {
                        // Tạo câu lệnh CREATE PRIMARY KEY
                        createQuery = String.format("ALTER TABLE %s.%s DROP PRIMARY KEY", schemaName, tableName);
                        createNativeQuery = session.createNativeQuery(createQuery);
                        createNativeQuery.executeUpdate();
                    }

                    break;
                case "FOREIGN KEY":
                    // Tạo câu lệnh CREATE FOREIGN KEY
                    createQuery = String.format("ALTER TABLE %s.%s DROP FOREIGN KEY %s", schemaName, tableName, constraintName);
                    createNativeQuery = session.createNativeQuery(createQuery);
                    createNativeQuery.executeUpdate();
                    break;
                case "UNIQUE":
                    // Tạo câu lệnh DROP UNIQUE
                    createQuery = String.format("ALTER TABLE %s.%s DROP INDEX %s", schemaName, tableName, constraintName);
                    createNativeQuery = session.createNativeQuery(createQuery);
                    createNativeQuery.executeUpdate();
                    break;
                case "CHECK":
                    // Tạo câu lệnh CREATE CHECK
                    createQuery = String.format("ALTER TABLE %s.%s DROP CHECK %s", schemaName, tableName, constraintName);
                    createNativeQuery = session.createNativeQuery(createQuery);
                    createNativeQuery.executeUpdate();
                    break;
                default:
                    check = false;
                    break;
            }
            if (!check) {
                return false;
            }

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
    public Constraint update(SessionFactory sessionFactory, Constraint constraint, String oldConstraintName) {
        boolean success = false;
        Session session = null;

        boolean afterDelete = false;
        boolean afterAdd = false;
        Constraint backUpConstraint = null;

        try {
            session = sessionFactory.openSession();
            //Open transaction
            session.beginTransaction();

            String schemaName = constraint.getSchemaName();
            String tableName = constraint.getTableName();
            String constraintName = constraint.getName();
            String constraintType = constraint.getConstraintType();
            String refTableName = constraint.getRefTableName();
            String refColumnName = constraint.getRefColumnName();
            String expression = constraint.getExpression();
            String[] columnList = constraint.getColumnList().toArray(new String[0]);


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

            String dropQuery;
            String createQuery;
            String updateQuery;
            NativeQuery<?> dropNativeQuery;
            NativeQuery<?> createNativeQuery;
            NativeQuery<?> updateNativeQuery;

            boolean check = true;

            switch (constraintType) {
                case "PRIMARY KEY":
                    // Tạo câu lệnh UPDATE PRIMARY KEY
                    updateQuery = String.format("ALTER TABLE %s.%s DROP PRIMARY KEY, ADD PRIMARY KEY(%s)", schemaName, tableName, columns);

                    updateNativeQuery = session.createNativeQuery(updateQuery);
                    updateNativeQuery.executeUpdate();

                    break;
                case "FOREIGN KEY":
                    // Tạo câu lệnh CREATE FOREIGN KEY

                    backUpConstraint = getConstraint(sessionFactory, null, schemaName, tableName, oldConstraintName);

                    dropQuery = String.format("ALTER TABLE %s.%s DROP FOREIGN KEY %s", schemaName, tableName, oldConstraintName);
                    dropNativeQuery = session.createNativeQuery(dropQuery);
                    dropNativeQuery.executeUpdate();

                    afterDelete = true;

                    createQuery = String.format("ALTER TABLE %s.%s ADD CONSTRAINT %s FOREIGN KEY (%s) REFERENCES %s.%s(%s)", schemaName, tableName, constraintName, columns, schemaName, refTableName, refColumnName);
                    createNativeQuery = session.createNativeQuery(createQuery);
                    createNativeQuery.executeUpdate();
                    afterAdd = true;

                    break;
                case "UNIQUE":
                    // Tạo câu lệnh UPDATE UNIQUE
                    updateQuery = String.format("ALTER TABLE %s.%s DROP INDEX %s, ADD UNIQUE KEY %s (%s)", schemaName, tableName, oldConstraintName, constraintName, columns);
                    updateNativeQuery = session.createNativeQuery(updateQuery);
                    updateNativeQuery.executeUpdate();

                    break;
                case "CHECK":
                    // Tạo câu lệnh CREATE CHECK
                    createQuery = String.format("ALTER TABLE %s.%s DROP CHECK %s", schemaName, tableName, constraintName);
                    break;
                default:
                    check = false;
                    break;
            }
            if (!check) {
                throw new RuntimeException("Constraint type is not valid");
            }

            session.getTransaction().commit();
            success = true;
            return constraint;

        } catch (GenericJDBCException | SQLGrammarException e) {
            if (afterDelete && !afterAdd) {
                save(sessionFactory, backUpConstraint);
            }

            session.getTransaction().rollback();
            throw e;
        } catch (Exception e) {
            if (afterDelete && !afterAdd) {
                save(sessionFactory, backUpConstraint);

            }
            session.getTransaction().rollback();
            System.out.println(e.getLocalizedMessage());
            System.out.println(e.getMessage());

            return null;

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
}
