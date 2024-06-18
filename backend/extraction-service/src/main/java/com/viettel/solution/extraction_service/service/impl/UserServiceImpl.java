package com.viettel.solution.extraction_service.service.impl;

import com.viettel.solution.extraction_service.dto.UserDto;
import com.viettel.solution.extraction_service.entity.TemplateUser;
import com.viettel.solution.extraction_service.entity.User;
import com.viettel.solution.extraction_service.repository.TemplateUserRepository;
import com.viettel.solution.extraction_service.repository.UserRepository;
import com.viettel.solution.extraction_service.service.AwsService;
import com.viettel.solution.extraction_service.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TemplateUserRepository templateUserRepository;

    @Autowired
    private AwsService awsService;

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
        if (user != null) {
            userRepository.save(user);
        }

    }

    @Override
    @Transactional
    public TemplateUser addTemplate(Long id, MultipartFile template) {
        try {
            Optional<User> result = userRepository.findById(id);
            User user = null;
            if (result.isPresent()) {
                user = result.get();
            }

            if (user != null) {
                String fileName = StringUtils.cleanPath(Objects.requireNonNull(template.getOriginalFilename()));
                String contentType = template.getContentType();
                long fileSize = template.getSize();
                InputStream inputStream = template.getInputStream();

                awsService.uploadFile(id + "/" + fileName, fileSize, contentType, inputStream);

                TemplateUser newTemplate = new TemplateUser();
                newTemplate.setTemplateName(fileName);
                newTemplate.setUser(user);

                return (templateUserRepository.save(newTemplate));

            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to store file");
        }
        return null;
    }

    @Override
    @Transactional
    public List<String> getListTemplate(Long id) {
        try {
            Optional<User> result = userRepository.findById(id);

            User user = null;
            if (result.isPresent()) {
                user = result.get();
            }

            if (user == null) {
                return null;
            }
            List<TemplateUser> templates = user.getTemplates();

            List<String> listTemplate = new ArrayList<>();

            List<String> listKeys = awsService.listFiles();
            for (String key : listKeys) {
                if (key.startsWith(id + "/")) {
                    listTemplate.add(key.substring(key.indexOf("/") + 1));
                }
            }

            return listTemplate;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to get list template");
        }
    }
}
