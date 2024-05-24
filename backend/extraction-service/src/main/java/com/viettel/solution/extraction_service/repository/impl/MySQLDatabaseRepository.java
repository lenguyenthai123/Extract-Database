package com.viettel.solution.extraction_service.repository.impl;

import com.viettel.solution.extraction_service.entity.Column;
import com.viettel.solution.extraction_service.entity.DatabaseConfig;
import com.viettel.solution.extraction_service.entity.DatabaseStructure;
import com.viettel.solution.extraction_service.entity.Table;
import com.viettel.solution.extraction_service.repository.DatabaseRepository;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;

@Repository
public class MySQLDatabaseRepository implements DatabaseRepository {

    private String MYSQL_DRIVER = "com.mysql.cj.jdbc.Driver";

    @Override
    public DatabaseStructure getDatabaseStructure(DatabaseConfig databaseConfigEntity) {
        Connection connection = null;
        try {
            connection = connect(databaseConfigEntity);
            DatabaseMetaData metaData = getDatabaseMetaData(connection);
            if (metaData == null) {
                return null;
            } else {
                return DatabaseStructure.createDatabaseEntity(metaData);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            disconnect(connection);
        }
    }

    @Override
    public Table getTableStructure(DatabaseConfig databaseConfigEntity, String tableName) {
        Connection connection = null;
        try {
            connection = connect(databaseConfigEntity);
            DatabaseMetaData metaData = getDatabaseMetaData(connection);
            if (metaData == null) {
                return null;
            } else {
                return Table.createTableEntity(metaData, tableName);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            disconnect(connection);
        }
    }

    @Override
    public Column getColumnStructure(DatabaseConfig databaseConfigEntity, String tableName, String columnName) {
        Connection connection = null;
        try {
            connection = connect(databaseConfigEntity);
            DatabaseMetaData metaData = getDatabaseMetaData(connection);
            if (metaData == null) {
                return null;
            } else {
                return Column.createColumnEntity(metaData, tableName, columnName);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            disconnect(connection);
        }
    }


    @Override
    public DatabaseMetaData getDatabaseMetaData(Connection connection) {
        try {
            if (connection == null) {
                return null;
            }
            DatabaseMetaData metaData = connection.getMetaData();
            return metaData;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Connection connect(DatabaseConfig config) {
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

    @Override
    public void disconnect(Connection connection) {
        try {

            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Error while disconnecting from MySQL database.");
            e.printStackTrace();
        }
    }


}
