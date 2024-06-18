package com.viettel.solution.extraction_service.service;

import com.viettel.solution.extraction_service.dto.UserDto;
import com.viettel.solution.extraction_service.entity.User;

public interface UserService {

    public UserDto saveUser(User user);

    public void deleteUser(UserDto user);

    public UserDto getUserById(Long id);

    public UserDto getUserByUsername(String username);

    public UserDto getUserByUsernameAndPassword(String username, String password);

    public void updateUser(User user);
}
