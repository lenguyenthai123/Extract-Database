package com.viettel.solution.extraction_service.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.viettel.solution.extraction_service.entity.Index;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class IndexDto {
    @NotBlank(message = "usernameId is required")
    private String usernameId;

    private String databaseName;

    @NotBlank(message = "Schema name is required")
    private String schemaName;

    @NotBlank(message = "Table name is required")
    private String tableName;

    @NotBlank(message = "Type of database is required")
    private String type;

    private String oldName; // Đại diện cho ID;

    String name;
    List<String> columns;


    public IndexDto() {
        this.columns = new ArrayList<>();
        this.tableName = null;
    }

    public IndexDto(Index index) {
        this.columns = new ArrayList<>();
        this.columns.addAll(index.getColumns());
        this.name = index.getName();
        this.tableName = index.getTableName();
    }
    @JsonIgnore
    public String getTableId() {
        return usernameId + type + schemaName  + tableName;
    }

    // Nếu bạn muốn serialize giá trị tableId khi chuyển đổi đối tượng thành JSON
    @JsonProperty("tableId")
    public String getSerializedTableId() {
        return getTableId();
    }
}
