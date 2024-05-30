package com.viettel.solution.extraction_service.entity;


import com.viettel.solution.extraction_service.dto.ColumnDto;
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

    private String databaseName;
    private String schemaName;
    private String tableName;
    private String type;

    private String name;
    private String dataType;
    private Integer size;
    private boolean isPrimaryKey;
    private boolean nullable;
    private boolean autoIncrement;
    private String defaultValue;
    private String description;


    public Column(ColumnDto columnDto) {
        this.databaseName = columnDto.getDatabaseName();
        this.schemaName = columnDto.getSchemaName();
        this.tableName = columnDto.getTableName();
        this.type = columnDto.getType();
        this.name = columnDto.getName();
        this.dataType = columnDto.getDataType();
        this.size = columnDto.getSize();
        this.isPrimaryKey = columnDto.isPrimaryKey();
        this.nullable = columnDto.isNullable();
        this.autoIncrement = columnDto.isAutoIncrement();
        this.defaultValue = columnDto.getDefaultValue();
        this.description = columnDto.getDescription();
    }
}
