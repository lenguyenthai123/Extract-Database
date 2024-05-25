package com.viettel.solution.extraction_service.entity;


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
public class Schema {
    private String name;
    private List<Table> tables;

    public Schema() {
        this.tables = new ArrayList<>();
    }


    // This function create for MySQL


}
