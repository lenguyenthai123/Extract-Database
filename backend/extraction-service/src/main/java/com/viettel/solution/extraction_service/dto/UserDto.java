package com.viettel.solution.extraction_service.dto;

import com.viettel.solution.extraction_service.entity.User;
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
public class UserDto {


    private Long id;

    @NotBlank(message = "username is required")
    private String username;

    public UserDto(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
    }
}
