package com.viettel.solution.extraction_service.repository;


import com.viettel.solution.extraction_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);

    User findByUsernameAndPassword(String username, String password);

    Optional<User> findById(Long id);


}
