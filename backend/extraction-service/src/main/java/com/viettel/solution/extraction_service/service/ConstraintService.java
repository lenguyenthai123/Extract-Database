package com.viettel.solution.extraction_service.service;

import com.viettel.solution.extraction_service.dto.ConstraintDto;
import com.viettel.solution.extraction_service.dto.IndexDto;
import com.viettel.solution.extraction_service.dto.RequestDto;
import org.apache.coyote.Request;
import org.hibernate.mapping.Constraint;

import java.util.List;

public interface ConstraintService {

    public ConstraintDto save(String type, String usernameId, ConstraintDto constraintDto);

    public List<ConstraintDto> getListFromTable(String type, String usernameId, String schemaName, String tableName);

    public ConstraintDto update(String type, String usernameId, ConstraintDto constraintDto);

    public boolean delete(String type, String usernameId, ConstraintDto constraintDto);
}
