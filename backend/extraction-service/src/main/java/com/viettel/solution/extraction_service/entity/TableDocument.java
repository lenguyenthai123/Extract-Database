package com.viettel.solution.extraction_service.entity;

import com.viettel.solution.extraction_service.utils.Utils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
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
    @Field(name = "usernameId")
    private String usernameId;
    private String schemaName;
    private String name;
    private List<Column> columns;
    private List<Constraint> constraints;
    private List<Index> indexs;
    private List<Trigger> triggers;
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


        this.columns.addAll(table.getColumns());
        this.constraints.addAll(table.getConstraints());
        this.indexs.addAll(table.getIndexs());
        this.triggers.addAll(table.getTriggers());
    }
}
