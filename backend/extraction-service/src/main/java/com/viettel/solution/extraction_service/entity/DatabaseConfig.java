package com.viettel.solution.extraction_service.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Data               // Bao gồm @Getter, @Setter, @ToString, @EqualsAndHashCode, và @RequiredArgsConstructor
@NoArgsConstructor  // Tạo constructor không tham số
@AllArgsConstructor // Tạo constructor với tất cả các tham số
@Builder            // Tạo Builder pattern
public class DatabaseConfig {

    private String url;
    private String username;
    private String password;
    private String type;
    private String host;
    private String port;
    private String databaseName;
    private String schema;
    private String driverClassName;
    private String jdbcUrl;
    private String connectionProperties;
    private String usernameId;
    // For Oracle
    private String serviceName;
}
