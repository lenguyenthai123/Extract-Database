package com.viettel.solution.extraction_service.database;

import com.viettel.solution.extraction_service.entity.DatabaseConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class DatabaseConnection {

    static private String MYSQL_DRIVER = "com.mysql.cj.jdbc.Driver";
    static private String ORACLE_DRIVER = "oracle.jdbc.driver.OracleDriver";
    static private String MARIADB_DRIVER = "org.mariadb.jdbc.Driver";

    static public Map<String, Connection> connectionMySqlMap;
    static public Map<String, Connection> connectionOracleMap;
    static public Map<String, Connection> connectionMariaDbMap;

    static {
        connectionMySqlMap = new HashMap<>();
        connectionOracleMap = new HashMap<>();
        connectionMariaDbMap = new HashMap<>();
    }

    static public synchronized Connection getConnection(String usernameId, String type) {
        if (type.equals("mysql")) {
            return connectionMySqlMap.get(usernameId);
        } else if (type.equals("oracle")) {
            return connectionOracleMap.get(usernameId);
        } else if (type.equals("mariadb")) {
            return connectionMariaDbMap.get(usernameId);
        }
        return null;
    }

    static public synchronized boolean closeConnection(String usernameId, String type) {
        Connection connection = null;
        if (type.equals("mysql")) {
            connection = connectionMySqlMap.get(usernameId);
            connectionMySqlMap.remove(usernameId);
        } else if (type.equals("oracle")) {
            connection = connectionOracleMap.get(usernameId);
            connectionOracleMap.remove(usernameId);
        } else if (type.equals("mariadb")) {
            connection = connectionMariaDbMap.get(usernameId);
            connectionMariaDbMap.remove(usernameId);
        }
        disconnect(connection);
        return false;
    }

    static private void disconnect(Connection connection) {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Error while disconnecting from MySQL database.");
            e.printStackTrace();
        }
    }

    static public synchronized boolean createConnection(String usernameId, DatabaseConfig databaseConfig) {
        Connection connection = null;
        if (databaseConfig.getType().equalsIgnoreCase("mysql")) {
            connection = createConnectionMySql(databaseConfig);
            connectionMySqlMap.put(usernameId, connection);
        } else if (databaseConfig.getType().equalsIgnoreCase("oracle")) {
            connection = createConnectionOracle(databaseConfig);
            connectionOracleMap.put(usernameId, connection);
        } else if (databaseConfig.getType().equalsIgnoreCase("mariadb")) {
            connection = createConnectionMariaDb(databaseConfig);
            connectionMariaDbMap.put(usernameId, connection);
        }
        return connection != null;
    }

    static private Connection createConnectionMySql(DatabaseConfig config) {

        try {
            // Tải driver MySQL
            Class.forName(MYSQL_DRIVER);
            // Kết nối đến cơ sở dữ liệu MySQL
            Connection connection = DriverManager.getConnection("jdbc:mysql://" + config.getHost() + ":" + config.getPort() + "/" + config.getDatabaseName(), config.getUsername(), config.getPassword());
            System.out.println("Connected to MySQL database.");

            return connection;
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    static private Connection createConnectionOracle(DatabaseConfig config) {

        try {
            // Tải driver Oracle
            Class.forName(ORACLE_DRIVER);
            // Kết nối đến cơ sở dữ liệu Oracle
            Connection connection = DriverManager.getConnection("jdbc:oracle:thin:@" + config.getHost() + ":" + config.getPort() + ":" + config.getDatabaseName(), config.getUsername(), config.getPassword());
            System.out.println("Connected to Oracle database.");

            return connection;
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    static private Connection createConnectionMariaDb(DatabaseConfig config) {

        try {
            // Tải driver MariaDb
            Class.forName(MARIADB_DRIVER);
            // Kết nối đến cơ sở dữ liệu MariaDb
            Connection connection = DriverManager.getConnection("jdbc:mariadb://" + config.getHost() + ":" + config.getPort() + "/" + config.getDatabaseName(), config.getUsername(), config.getPassword());
            System.out.println("Connected to MariaDb database.");

            return connection;
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
