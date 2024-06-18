package com.viettel.solution.extraction_service.service;

import com.viettel.solution.extraction_service.dto.UserDto;
import com.viettel.solution.extraction_service.entity.TemplateUser;
import com.viettel.solution.extraction_service.entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {

    public UserDto saveUser(User user);

    public void deleteUser(UserDto user);

    public UserDto getUserById(Long id);

    public UserDto getUserByUsername(String username);

    public UserDto getUserByUsernameAndPassword(String username, String password);

    public void updateUser(User user);

    public TemplateUser addTemplate(Long id, MultipartFile template);

    public List<String> getListTemplate(Long id);
}
