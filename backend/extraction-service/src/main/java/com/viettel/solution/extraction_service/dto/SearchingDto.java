package com.viettel.solution.extraction_service.dto;

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
public class SearchingDto {

    @NotBlank(message = "usernameId is required")
    String usernameId;
    @NotBlank(message =  "Type is required")
    String type;

    @NotBlank(message = "Key word is needed")
    String keyword;
}
