package com.viettel.solution.extraction_service.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

@Component
@Data               // Bao gồm @Getter, @Setter, @ToString, @EqualsAndHashCode, và @RequiredArgsConstructor
@AllArgsConstructor // Tạo constructor với tất cả các tham số
@NoArgsConstructor
@Builder
public class Column {

    private String name;
    private String dataType;
    private Integer size;
    private boolean isPrimaryKey;
    private boolean nullable;
    private boolean autoIncrement;
    private String defaultValue;
    private String description;


    static public Column createColumnEntity(DatabaseMetaData metaData, String tableName, String columnNameKey) throws SQLException {
        ResultSet columnsResultSet = metaData.getColumns(null, null, tableName, "%");

        // Lấy thông tin các khóa chính
        Set<String> primaryKeys = new HashSet<>();
        ResultSet primaryKeysResultSet = metaData.getPrimaryKeys(null, null, tableName);
        while (primaryKeysResultSet.next()) {
            primaryKeys.add(primaryKeysResultSet.getString("COLUMN_NAME"));
        }


        while (columnsResultSet.next()) {
            String columnName = columnsResultSet.getString("COLUMN_NAME");

            if (columnName.equals(columnNameKey)) {
                String dataType = columnsResultSet.getString("TYPE_NAME");
                Integer columnSize = columnsResultSet.getInt("COLUMN_SIZE");
                boolean isPrimaryKey = primaryKeys.contains(columnName);
                boolean nullable = "YES".equals(columnsResultSet.getString("IS_NULLABLE"));
                boolean autoIncrement = "YES".equals(columnsResultSet.getString("IS_AUTOINCREMENT"));
                String defaultValue = columnsResultSet.getString("COLUMN_DEF");
                String description = columnsResultSet.getString("REMARKS");

                return Column.builder()
                        .name(columnName)
                        .dataType(dataType)
                        .size(columnSize)
                        .isPrimaryKey(isPrimaryKey)
                        .nullable(nullable)
                        .autoIncrement(autoIncrement)
                        .defaultValue(defaultValue)
                        .description(description)
                        .build();
            }
        }
        return null;
    }
}
