package com.viettel.solution.extraction_service.dto;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
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

    private String tableName;
    private String column;
    private String constraint;
    private String index;
    private String view;

    @JsonIgnore
    public String getTableId() {
        return usernameId + type + schemaName + tableName;
    }

    @JsonIgnore
    public String getDatabaseId() {
        return usernameId + type;
    }

    // Nếu bạn muốn serialize giá trị tableId khi chuyển đổi đối tượng thành JSON
    @JsonProperty("tableId")
    public String getSerializedTableId() {
        return getTableId();
    }
}
