package com.viettel.solution.extraction_service.utils;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

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
}
