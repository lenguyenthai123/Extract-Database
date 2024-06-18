package com.viettel.solution.extraction_service.service.impl;

import com.viettel.solution.extraction_service.dto.UserDto;
import com.viettel.solution.extraction_service.entity.User;
import com.viettel.solution.extraction_service.service.AuthService;
import com.viettel.solution.extraction_service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {
    @Autowired
    private UserService userService;

    @Override
    public UserDto login(User user) {
        return userService.getUserByUsernameAndPassword(user.getUsername(), user.getPassword());
    }

    @Override
    public UserDto register(User user) {
        if (userService.getUserByUsername(user.getUsername()) != null) {
            return null;
        }
        return userService.saveUser(user);
    }
}
