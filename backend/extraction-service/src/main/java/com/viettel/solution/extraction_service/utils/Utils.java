package com.viettel.solution.extraction_service.utils;

import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Environment;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class Utils {
    static public DatabaseMetaData getDatabaseMetaData(Connection connection) {
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
    public static SessionFactory getSessionFactory(String dialect, String driverClass, String url, String username, String password) {
        Map<String, Object> settings = new HashMap<>();
        settings.put(Environment.DIALECT, dialect);
        settings.put(Environment.DRIVER, driverClass);
        settings.put(Environment.URL, url);
        settings.put(Environment.USER, username);
        settings.put(Environment.PASS, password);
        settings.put(Environment.HBM2DDL_AUTO, "update");
        settings.put(Environment.SHOW_SQL, true);
        settings.put(Environment.FORMAT_SQL, true);

        StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .applySettings(settings)
                .build();

        Metadata metadata = new MetadataSources(registry).getMetadataBuilder().build();
        return metadata.getSessionFactoryBuilder().build();
    }
}
