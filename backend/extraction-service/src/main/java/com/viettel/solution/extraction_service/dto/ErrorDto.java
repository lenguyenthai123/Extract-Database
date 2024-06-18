package com.viettel.solution.extraction_service.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Data               // Bao gồm @Getter, @Setter, @ToString, @EqualsAndHashCode, và @RequiredArgsConstructor
@AllArgsConstructor // Tạo constructor với tất cả các tham số
@Builder
@NoArgsConstructor
public class ErrorDto {

    private String message;
    private String code;

    public ErrorDto(String message) {
        this.message = message;
    }
}
