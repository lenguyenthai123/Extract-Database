package com.viettel.solution.extraction_service.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
@Data               // Bao gồm @Getter, @Setter, @ToString, @EqualsAndHashCode, và @RequiredArgsConstructor
@AllArgsConstructor // Tạo constructor với tất cả các tham số
@Builder
public class Table {


    private String name;
    private List<Column> columns;
    private List<Constraint> constraints;
    private List<Index> indexs;
    private List<Trigger> triggers;
    private String description;

    public Table() {
        this.columns = new ArrayList<>();
        this.constraints = new ArrayList<>();
        this.indexs = new ArrayList<>();
        this.triggers = new ArrayList<>();
    }


}
