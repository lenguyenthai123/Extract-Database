package com.viettel.solution.extraction_service.controller;

import com.viettel.solution.extraction_service.dto.MessageDto;
import com.viettel.solution.extraction_service.dto.RegistrationDto;
import com.viettel.solution.extraction_service.dto.UserDto;
import com.viettel.solution.extraction_service.entity.User;
import com.viettel.solution.extraction_service.service.AuthService;
import com.viettel.solution.extraction_service.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid User user) {

        if (user.getUsername() == null || user.getPassword() == null) {
            return ResponseEntity.badRequest().body(new MessageDto("Username and password are required"));
        }

        UserDto userDto = authService.login(user);

        if (userDto == null) {
            return ResponseEntity.badRequest().body(new MessageDto("Invalid username or password"));
        }
        return ResponseEntity.ok(userDto);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegistrationDto registrationDto) {

        if (!registrationDto.getPassword().equals(registrationDto.getConfirmPassword())) {
            return ResponseEntity.badRequest().body(new MessageDto("Password and confirm password do not match"));
        }

        if (userService.getUserByUsername(registrationDto.getUsername()) != null) {
            return ResponseEntity.badRequest().body(MessageDto.builder().message("Username already exists").build());
        }
        User user = new User();
        user.setUsername(registrationDto.getUsername());
        user.setPassword(registrationDto.getPassword());

        UserDto userDto = authService.register(user);
        return ResponseEntity.ok(userDto);
    }

}
