package com.viettel.solution.extraction_service.entity;

import com.viettel.solution.extraction_service.utils.Utils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Data               // Bao gồm @Getter, @Setter, @ToString, @EqualsAndHashCode, và @RequiredArgsConstructor
@AllArgsConstructor // Tạo constructor với tất cả các tham số
@Builder
@Document(indexName = "table")
public class TableDocument {

    @Id
    private String id;

    @Field(type = FieldType.Text)
    private String type;

    @Field(name = "usernameId", type = FieldType.Keyword)
    private String usernameId;

    @Field(type = FieldType.Keyword)
    private String schemaName;

    @Field(type = FieldType.Keyword)
    private String name;

    @Field(type = FieldType.Nested, includeInParent = true)
    private List<Column> columns;

    @Field(type = FieldType.Nested, includeInParent = true)
    private List<Constraint> constraints;

    @Field(type = FieldType.Nested, includeInParent = true)
    private List<Index> indexs;

    @Field(type = FieldType.Nested, includeInParent = true)
    private List<Trigger> triggers;

    @Field(type = FieldType.Text)
    private String description;

    public TableDocument() {
        this.columns = new ArrayList<>();
        this.constraints = new ArrayList<>();
        this.indexs = new ArrayList<>();
        this.triggers = new ArrayList<>();
        this.id = Utils.generateUUID();
    }

    public TableDocument(Table table) {
        this.columns = new ArrayList<>();
        this.constraints = new ArrayList<>();
        this.indexs = new ArrayList<>();
        this.triggers = new ArrayList<>();
        this.id = Utils.generateUUID();
        this.schemaName = table.getSchemaName();
        this.name = table.getName();


        this.columns.addAll(table.getColumns());
        this.constraints.addAll(table.getConstraints());
        this.indexs.addAll(table.getIndexs());
        this.triggers.addAll(table.getTriggers());
    }

    public void copy(Table table)
    {
        this.columns = new ArrayList<>();
        this.constraints = new ArrayList<>();
        this.indexs = new ArrayList<>();
        this.triggers = new ArrayList<>();
        this.schemaName = table.getSchemaName();
        this.name = table.getName();

        this.columns.addAll(table.getColumns());
        this.constraints.addAll(table.getConstraints());
        this.indexs.addAll(table.getIndexs());
        this.triggers.addAll(table.getTriggers());
    }
}
