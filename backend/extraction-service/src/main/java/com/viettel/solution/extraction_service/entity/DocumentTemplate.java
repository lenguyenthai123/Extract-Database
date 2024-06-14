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
public class DocumentTemplate {

    private String usernameId;
    private String type;
    private String headerRight;
    private String headerLeft;
    private String footerLeft;
    private String size;
    private String color;
    private String font;
    private boolean isDefault;
}
