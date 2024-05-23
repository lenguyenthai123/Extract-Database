package com.viettel.solution.extraction_service.entity;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
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
public class DatabaseEntity {

    private List<TableEntity> tables;

    public DatabaseEntity() {
        this.tables = new ArrayList<>();
    }

    static public DatabaseEntity createDatabaseEntity(DatabaseMetaData metaData) throws SQLException {

        DatabaseEntity databaseEntity = new DatabaseEntity();
        ResultSet tablesResultSet = metaData.getTables(null, null, "%", new String[]{"TABLE"});

        while (tablesResultSet.next()) {

            Map<String, Object> table = new HashMap<>();
            String tableName = tablesResultSet.getString("TABLE_NAME");
            if (tableName.equals("sys_config")) {
                continue;
            }
            TableEntity tableEntity = TableEntity.createTableEntity(metaData, tableName);
            databaseEntity.tables.add(tableEntity);
        }
        return databaseEntity;
    }

}
