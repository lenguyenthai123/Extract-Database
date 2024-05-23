package com.viettel.solution.extraction_service.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Data               // Bao gồm @Getter, @Setter, @ToString, @EqualsAndHashCode, và @RequiredArgsConstructor
@AllArgsConstructor // Tạo constructor với tất cả các tham số
@NoArgsConstructor
@Builder
public class ColumnEntity {

    private String columnName;

    private String dataType;

    private Integer columnSize;

    private boolean isPrimaryKey;

}
