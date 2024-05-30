package com.viettel.solution.extraction_service.repository.impl;

import org.hibernate.Session;

public class CommonRepository {

    public static boolean tableExists(Session session, String tableName) {
        String query = "SELECT 1 FROM information_schema.tables WHERE table_name = :table";
        Object result = session.createNativeQuery(query)
                .setParameter("table", tableName)
                .uniqueResult();
        return result != null;
    }

    public static boolean columnExists(Session session, String tableName, String columnName) {
        String query = "SELECT 1 FROM information_schema.columns WHERE table_name = :table AND column_name = :column";
        Object result = session.createNativeQuery(query)
                .setParameter("table", tableName)
                .setParameter("column", columnName)
                .uniqueResult();
        return result != null;
    }
}
