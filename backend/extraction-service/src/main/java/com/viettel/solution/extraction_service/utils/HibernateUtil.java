package com.viettel.solution.extraction_service.utils;

import com.viettel.solution.extraction_service.entity.DatabaseConfig;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.HashMap;
import java.util.Map;

public class HibernateUtil {
    public static SessionFactory createSessionFactory(DatabaseConfig config) {
        Configuration configuration = new Configuration();

        switch (config.getType().toLowerCase()) {
            case "mysql":
                configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
                configuration.setProperty("hibernate.connection.driver_class", "com.mysql.cj.jdbc.Driver");
                configuration.setProperty("hibernate.connection.url", "jdbc:mysql://" + config.getHost() + ":" + config.getPort() + "/" + config.getDatabaseName());
                break;
            case "oracle":
                configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.OracleDialect");
                configuration.setProperty("hibernate.connection.driver_class", "oracle.jdbc.OracleDriver");
                configuration.setProperty("hibernate.connection.url", "jdbc:oracle:thin:@" + config.getHost() + ":" + config.getPort() + ":" + config.getServiceName());
                break;
            case "mariadb":
                configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.MariaDBDialect");
                configuration.setProperty("hibernate.connection.driver_class", "org.mariadb.jdbc.Driver");
                configuration.setProperty("hibernate.connection.url", "jdbc:mariadb://" + config.getHost() + ":" + config.getPort() + "/" + config.getDatabaseName());
                break;
            default:
                throw new IllegalArgumentException("Unsupported database type: " + config.getType());
        }
        //configuration.setProperty("hibernate.connection.autocommit", "false");
        configuration.setProperty("hibernate.connection.username", config.getUsername());
        configuration.setProperty("hibernate.connection.password", config.getPassword());

        return configuration.buildSessionFactory();
    }
}