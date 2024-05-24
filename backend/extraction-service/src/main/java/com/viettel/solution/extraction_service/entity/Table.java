package com.viettel.solution.extraction_service.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
@Data               // Bao gồm @Getter, @Setter, @ToString, @EqualsAndHashCode, và @RequiredArgsConstructor
@AllArgsConstructor // Tạo constructor với tất cả các tham số
@Builder
public class Table {


    private String tableName;

    private List<Column> columns;

    private List<Map<String, Object>> data;

    public Table() {
        this.columns = new ArrayList<>();
        this.data = new ArrayList<>();
    }

    public static Table createTableEntity(DatabaseMetaData metaData, String tableName) throws SQLException {
        Table tableEntity = new Table();
        tableEntity.tableName = tableName;

        ResultSet columnsResultSet = metaData.getColumns(null, null, tableName, "%");

        // Lấy thông tin các khóa chính
        Set<String> primaryKeys = new HashSet<>();
        ResultSet primaryKeysResultSet = metaData.getPrimaryKeys(null, null, tableName);
        while (primaryKeysResultSet.next()) {
            primaryKeys.add(primaryKeysResultSet.getString("COLUMN_NAME"));
        }

        while (columnsResultSet.next()) {
            String columnName = columnsResultSet.getString("COLUMN_NAME");
            String dataType = columnsResultSet.getString("TYPE_NAME");
            Integer columnSize=columnsResultSet.getInt("COLUMN_SIZE");

            // Kiểm tra cột có phải là khóa chính không
            Boolean isPrimaryKey = primaryKeys.contains(columnName);

            Column column = Column.builder()
                    .columnName(columnName)
                    .dataType(dataType)
                    .columnSize(columnSize)
                    .isPrimaryKey(isPrimaryKey)
                    .build();

            tableEntity.columns.add(column);
        }

        return tableEntity;
    }
}
