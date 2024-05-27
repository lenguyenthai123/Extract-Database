package com.viettel.solution.extraction_service.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Data               // Bao gồm @Getter, @Setter, @ToString, @EqualsAndHashCode, và @RequiredArgsConstructor
@AllArgsConstructor // Tạo constructor với tất cả các tham số
@Builder
public class Database {

    private String name;
    private List<Schema> schemas;

    public Database() {
        this.schemas = new ArrayList<>();
    }


    public Database(List<Schema> schemas) {
        this.schemas = schemas;
    }

    // For oracle
    public Database(Schema schema) {
        this.schemas = new ArrayList<>();
        this.schemas.add(schema);
    }

}
