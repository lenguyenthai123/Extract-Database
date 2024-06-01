package com.viettel.solution.extraction_service.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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


    @NotBlank(message = "usernameId is required")
    private String usernameId;
    private String databaseName;
    @NotBlank(message = "schemaName is required")
    private String schemaName;

    @NotBlank(message = "Type of database is required")
    private String type;

    private String url;
    private String username;
    private String password;
    private String host;
    private String port;

    private String table;
    private String column;
    private String constraint;
    private String index;
    private String view;
}
