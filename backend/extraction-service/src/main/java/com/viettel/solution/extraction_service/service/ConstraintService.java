package com.viettel.solution.extraction_service.service;

import com.viettel.solution.extraction_service.dto.RequestDto;
import org.apache.coyote.Request;
import org.hibernate.mapping.Constraint;

import java.util.List;

public interface ConstraintService {

    public Constraint getConstraint(RequestDto requestDto);

    //public List<Constraint> getList();
}
