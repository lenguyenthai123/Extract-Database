package com.viettel.solution.extraction_service.repository;

import com.viettel.solution.extraction_service.entity.TemplateUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TemplateUserRepository extends JpaRepository<TemplateUser, Long> {

    public Optional<TemplateUser> findById(Long id);

}
