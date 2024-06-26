package com.viettel.solution.extraction_service.entity;


import com.viettel.solution.extraction_service.dto.IndexDto;
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
public class Index {

    String schemaName;
    String name;
    List<String> columns;
    String tableName;

    public Index() {
        this.columns = new ArrayList<>();
        this.tableName = null;
    }

    public Index(IndexDto indexdto) {
        this.schemaName = indexdto.getSchemaName();
        this.name = indexdto.getName();
        this.columns = new ArrayList<>();
        this.columns.addAll(indexdto.getColumns());
        this.tableName = indexdto.getTableName();
    }
}
