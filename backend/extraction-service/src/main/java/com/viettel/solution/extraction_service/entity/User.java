package com.viettel.solution.extraction_service.entity;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.viettel.solution.extraction_service.dto.UserDto;
import jakarta.persistence.Table;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

// Không được dùng @Data Lombok trong trường hợp này vì
// sẽ tạo ra vòng lặp vô tận khi sử dụng OneToMany

@Entity
@Table(name = "user")
public class User {
    // getters and setters
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Getter
    @NotBlank(message = "username is required")
    private String username;

    @Setter
    @Getter
    @NotBlank(message = "password is required")
    private String password;

    @Getter
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonManagedReference
    List<TemplateUser> templates;

    // for JPA only, no use
    public User() {

    }

    public User(UserDto userDto) {
        this.id = userDto.getId();
        this.username = userDto.getUsername();
    }


}
