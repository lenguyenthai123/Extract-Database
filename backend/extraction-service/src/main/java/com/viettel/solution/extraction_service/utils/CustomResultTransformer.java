package com.viettel.solution.extraction_service.utils;

import org.hibernate.transform.ResultTransformer;

import java.lang.reflect.Field;
import java.util.List;

public class CustomResultTransformer implements ResultTransformer {

    private final Class<?> resultClass;

    public CustomResultTransformer(Class<?> resultClass) {
        this.resultClass = resultClass;
    }

    @Override
    public Object transformTuple(Object[] tuple, String[] aliases) {
        try {
            Object result = resultClass.getDeclaredConstructor().newInstance();

            for (int i = 0; i < aliases.length; i++) {
                String alias = aliases[i];
                if (alias != null) {
                    String fieldName = convertColumnNameToFieldName(alias);
                    Field field = resultClass.getDeclaredField(fieldName);
                    field.setAccessible(true);
                    field.set(result, tuple[i]);
                }
            }

            return result;
        } catch (Exception e) {
            throw new RuntimeException("Failed to transform tuple to " + resultClass.getName(), e);
        }
    }

    @Override
    public List transformList(List collection) {
        return collection;
    }

    private String convertColumnNameToFieldName(String columnName) {
        // Convert column name to camelCase field name
        StringBuilder fieldName = new StringBuilder();
        boolean nextUpperCase = false;
        for (char c : columnName.toCharArray()) {
            if (c == '_') {
                nextUpperCase = true;
            } else {
                if (nextUpperCase) {
                    fieldName.append(Character.toUpperCase(c));
                    nextUpperCase = false;
                } else {
                    fieldName.append(Character.toLowerCase(c));
                }
            }
        }
        return fieldName.toString();
    }
}

