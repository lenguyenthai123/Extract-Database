package com.viettel.solution.extraction_service.database;

import com.viettel.solution.extraction_service.entity.DatabaseConfig;
import com.viettel.solution.extraction_service.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.jdbc.Work;

public class DatabaseConnection {
    static public Map<String, SessionFactory> sessionFactoryMySqlMap;
    static public Map<String, SessionFactory> sessionFactoryOracleMap;
    static public Map<String, SessionFactory> sessionFactoryMariaDbMap;

    static {
        sessionFactoryMySqlMap = new HashMap<>();
        sessionFactoryOracleMap = new HashMap<>();
        sessionFactoryMariaDbMap = new HashMap<>();
    }

    static public synchronized SessionFactory getSessionFactory(String usernameId, String type) {
        SessionFactory sessionFactory = null;
        switch (type.toLowerCase()) {
            case "mysql":
                sessionFactory = sessionFactoryMySqlMap.get(usernameId);
                break;
            case "oracle":
                sessionFactory = sessionFactoryOracleMap.get(usernameId);
                break;
            case "mariadb":
                sessionFactory = sessionFactoryMariaDbMap.get(usernameId);
                break;
            default:
                return null;
        }
        return sessionFactory;
    }

    static public synchronized boolean closeSessionFactory(String usernameId, String type) {
        SessionFactory sessionFactory = null;
        switch (type.toLowerCase()) {
            case "mysql":
                sessionFactory = sessionFactoryMySqlMap.remove(usernameId);
                break;
            case "oracle":
                sessionFactory = sessionFactoryOracleMap.remove(usernameId);
                break;
            case "mariadb":
                sessionFactory = sessionFactoryMariaDbMap.remove(usernameId);
                break;
            default:
                return false;
        }
        if (sessionFactory != null) {
            sessionFactory.close();
            return true;
        }

        return false;
    }

    static public synchronized boolean createSessionFactory(String usernameId, DatabaseConfig databaseConfig) {
        SessionFactory sessionFactory = HibernateUtil.createSessionFactory(databaseConfig);
        switch (databaseConfig.getType().toLowerCase()) {
            case "mysql":
                sessionFactoryMySqlMap.put(usernameId, sessionFactory);
                break;
            case "oracle":
                sessionFactoryOracleMap.put(usernameId, sessionFactory);
                break;
            case "mariadb":
                sessionFactoryMariaDbMap.put(usernameId, sessionFactory);
                break;
            default:
                return false;
        }
        return sessionFactory != null;
    }

    public static DatabaseMetaData getDatabaseMetaData(SessionFactory sessionFactory) {
        final DatabaseMetaData[] databaseMetaData = new DatabaseMetaData[1];
        // Open a session to interact with the database
        Session session = sessionFactory.openSession();

        // Use the Hibernate Work interface to execute a JDBC operation
        session.doWork(new Work() {
            @Override
            public void execute(Connection connection) throws SQLException {
                // Get the DatabaseMetaData from the connection
                databaseMetaData[0] = connection.getMetaData();
            }
        });
        // Close the session
        session.close();

        return databaseMetaData[0];
    }

    public static Connection getConnection(SessionFactory sessionFactory) {
        final Connection[] connectionHolder = new Connection[1];

        // Open a session to interact with the database
        try (Session session = sessionFactory.openSession()) {
            // Use the Hibernate Work interface to execute a JDBC operation
            session.doWork(new Work() {
                @Override
                public void execute(Connection connection) throws SQLException {
                    // Assign the connection to the connectionHolder array
                    connectionHolder[0] = connection;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        return connectionHolder[0];
    }

}

