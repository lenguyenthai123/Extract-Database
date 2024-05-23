package com.viettel.solution.extraction_service.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Data               // Bao gồm @Getter, @Setter, @ToString, @EqualsAndHashCode, và @RequiredArgsConstructor
@AllArgsConstructor // Tạo constructor với tất cả các tham số
@Builder
public class TableEntity {


    private String tableName;

    private List<Map<String, String>> columns;

    private List<Map<String, Object>> data;

    public TableEntity() {
        this.columns = new ArrayList<>();
        this.data = new ArrayList<>();
    }

    public static TableEntity createTableEntity(DatabaseMetaData metaData, String tableName) throws SQLException {
        TableEntity tableEntity = new TableEntity();
        tableEntity.tableName = tableName;

        tableEntity.columns = new ArrayList<>();
        ResultSet columnsResultSet = metaData.getColumns(null, null, tableName, "%");

        while (columnsResultSet.next()) {
            Map<String, String> column = new HashMap<>();
            column.put("columnName", columnsResultSet.getString("COLUMN_NAME"));
            column.put("dataType", columnsResultSet.getString("TYPE_NAME"));
            column.put("columnSize", columnsResultSet.getString("COLUMN_SIZE"));
            tableEntity.columns.add(column);
        }

        return tableEntity;
    }
}
