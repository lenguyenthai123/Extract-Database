package com.viettel.solution.extraction_service.entity;


import com.viettel.solution.extraction_service.dto.ConstraintDto;
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
public class Constraint {

    String name;
    String tableName;
    String schemaName;
    String columnName;
    String refTableName;
    String refColumnName;
    String constraintType;
    List<String> columnList;

    String expression;

    public Constraint()
    {
        this.columnList = new ArrayList<>();
    }
    public Constraint(ConstraintDto constraintDto) {
        this.name = constraintDto.getName();
        this.tableName = constraintDto.getTableName();
        this.schemaName = constraintDto.getSchemaName();
        this.columnName = constraintDto.getColumnName();
        this.refTableName = constraintDto.getRefTableName();
        this.refColumnName = constraintDto.getRefColumnName();
        this.constraintType = constraintDto.getConstraintType();
        this.columnList = new ArrayList<>();
        if (constraintDto.getColumnList() != null) {
            this.columnList.addAll(constraintDto.getColumnList());
        }
        this.expression = constraintDto.getExpression();
    }

    public Constraint(Constraint constraint) {
        this.name = constraint.getName();
        this.tableName = constraint.getTableName();
        this.schemaName = constraint.getSchemaName();
        this.columnName = constraint.getColumnName();
        this.refTableName = constraint.getRefTableName();
        this.refColumnName = constraint.getRefColumnName();
        this.constraintType = constraint.getConstraintType();
        this.columnList = new ArrayList<>();
        if (constraint.getColumnList() != null) {
            this.columnList.addAll(constraint.getColumnList());
        }
        this.expression = constraint.getExpression();

    }
}
