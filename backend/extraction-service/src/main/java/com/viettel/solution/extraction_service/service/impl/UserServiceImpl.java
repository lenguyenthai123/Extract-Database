package com.viettel.solution.extraction_service.service.impl;

import com.viettel.solution.extraction_service.dto.UserDto;
import com.viettel.solution.extraction_service.entity.User;
import com.viettel.solution.extraction_service.repository.UserRepository;
import com.viettel.solution.extraction_service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDto saveUser(User user) {
        return new UserDto(userRepository.save(user));
    }

    @Override
    public void deleteUser(UserDto user) {
        User userEntity = new User(user);
        userRepository.delete(userEntity);
        return;
    }

    @Override
    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id).orElse(null);
        if (Objects.isNull(user)) {
            return null;
        }
        return new UserDto(user);
    }

    @Override
    public UserDto getUserByUsername(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            return null;
        }
        return new UserDto(user);
    }

    @Override
    public UserDto getUserByUsernameAndPassword(String username, String password) {
        User user = userRepository.findByUsernameAndPassword(username, password);
        if (user == null) {
            return null;
        }
        return new UserDto(user);
    }

    @Override
    public void updateUser(User user) {
        if(user!=null)
        {
            userRepository.save(user);
        }

    }
}
