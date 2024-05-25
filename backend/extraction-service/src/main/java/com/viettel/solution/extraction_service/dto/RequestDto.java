package com.viettel.solution.extraction_service.dto;


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
public class RequestDto {

    private String usernameId;
    private String url;
    private String username;
    private String password;
    private String type;
    private String host;
    private String port;
    private String databaseName;
    private String schema;
    private String table;
    private String column;
    private String constraint;
    private String index;
    private String view;
}
