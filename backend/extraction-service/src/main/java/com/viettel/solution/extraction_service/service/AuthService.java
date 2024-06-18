package com.viettel.solution.extraction_service.service;

import com.viettel.solution.extraction_service.dto.UserDto;
import com.viettel.solution.extraction_service.entity.User;

public interface AuthService {

    public UserDto login(User user);

    public UserDto register(User user);

}
