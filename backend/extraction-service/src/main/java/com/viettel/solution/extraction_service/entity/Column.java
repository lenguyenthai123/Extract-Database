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

    private String columnName;

    private String dataType;

    private Integer columnSize;

    private boolean isPrimaryKey;
    private boolean isForeignKey;


    static public Column createColumnEntity(DatabaseMetaData metaData, String tableName, String columnNameKey) throws SQLException {
        ResultSet columnsResultSet = metaData.getColumns(null, null, tableName, "%");

        // Lấy thông tin các khóa chính
        Set<String> primaryKeys = new HashSet<>();
        ResultSet primaryKeysResultSet = metaData.getPrimaryKeys(null, null, tableName);
        while (primaryKeysResultSet.next()) {
            primaryKeys.add(primaryKeysResultSet.getString("COLUMN_NAME"));
        }

        // Lấy thông tin các khóa ngoại
        Set<String> foreignKeys = new HashSet<>();
        ResultSet foreignKeysResultSet = metaData.getImportedKeys(null, null, tableName);
        while (foreignKeysResultSet.next()) {
            foreignKeys.add(foreignKeysResultSet.getString("FKCOLUMN_NAME"));
        }


        while (columnsResultSet.next()) {
            String columnName = columnsResultSet.getString("COLUMN_NAME");

            if (columnName.equals(columnNameKey)) {

                String dataType = columnsResultSet.getString("TYPE_NAME");
                Integer columnSize = columnsResultSet.getInt("COLUMN_SIZE");

                // Kiểm tra cột có phải là khóa chính không
                Boolean isPrimaryKey = primaryKeys.contains(columnName);

                return Column.builder()
                        .columnName(columnName)
                        .dataType(dataType)
                        .columnSize(columnSize)
                        .isPrimaryKey(isPrimaryKey)
                        .build();
            }
        }
        return null;
    }
}
