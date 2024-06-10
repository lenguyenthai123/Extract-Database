package com.viettel.solution.extraction_service.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.viettel.solution.extraction_service.entity.Constraint;
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
@Builder
public class ConstraintDto {

    String type;
    String usernameId;

    String oldName;
    String name;
    String tableName;
    String schemaName;
    String columnName;
    String refTableName;
    String refColumnName;
    String constraintType;
    List<String> columnList;
    String expression;

    public ConstraintDto() {
        this.columnList = new ArrayList<>();
    }

    public ConstraintDto(Constraint constraint) {
        this.name = constraint.getName();
        this.tableName = constraint.getTableName();
        this.schemaName = constraint.getSchemaName();
        this.columnName = constraint.getColumnName();
        this.refTableName = constraint.getRefTableName();
        this.refColumnName = constraint.getRefColumnName();
        this.constraintType = constraint.getConstraintType();
        this.columnList = new ArrayList<>();
        this.expression = constraint.getExpression();
        if (constraint.getColumnList() != null) {
            this.columnList.addAll(constraint.getColumnList());
        }
    }

    @JsonIgnore
    public String getTableId() {
        return usernameId + type + schemaName + tableName;
    }

    // Nếu bạn muốn serialize giá trị tableId khi chuyển đổi đối tượng thành JSON
    @JsonProperty("tableId")
    public String getSerializedTableId() {
        return getTableId();
    }
}
