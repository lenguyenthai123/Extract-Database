package com.viettel.solution.extraction_service.dto;


import com.viettel.solution.extraction_service.entity.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Data               // Bao gồm @Getter, @Setter, @ToString, @EqualsAndHashCode, và @RequiredArgsConstructor
@AllArgsConstructor // Tạo constructor với tất cả các tham số
@NoArgsConstructor
@Builder
public class ColumnDto {

    // Indentify connection
    @NotBlank(message = "usernameId is required")
    private String usernameId;

    private String databaseName;

    @NotBlank(message = "Schema name is required")
    private String schemaName;

    @NotBlank(message = "Table name is required")
    private String tableName;

    @NotBlank(message = "Type of database is required")
    private String type;

    @NotBlank(message = "Column name is required")
    private String name;

    @NotBlank(message = "Data type is required")
    private String dataType;

    private Integer size;

    private boolean primaryKey;

    private boolean nullable;

    private boolean autoIncrement;

    private String defaultValue;
    private String description;


    //Convert from Column to ColumnDto
    public ColumnDto(Column column) {
        if (column == null)
            return;
        this.databaseName = column.getDatabaseName();
        this.schemaName = column.getSchemaName();
        this.tableName = column.getTableName();
        this.type = column.getType();
        this.name = column.getName();
        this.dataType = column.getDataType();
        this.size = column.getSize();
        this.primaryKey = column.isPrimaryKey();
        this.nullable = column.isNullable();
        this.autoIncrement = column.isAutoIncrement();
        this.defaultValue = column.getDefaultValue();
        this.description = column.getDescription();
    }

    public static List<ColumnDto> convertList(List<Column> columns) {
        List<ColumnDto> columnDtos = new ArrayList<>();
        for (Column column : columns) {
            columnDtos.add(new ColumnDto(column));
        }
        return columnDtos;
    }

}
