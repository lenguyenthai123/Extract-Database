package com.viettel.solution.extraction_service.service;

import com.viettel.solution.extraction_service.dto.RequestDto;
import org.apache.coyote.Request;
import org.hibernate.mapping.Constraint;

public interface ConstraintService {

    public Constraint getConstraint(RequestDto requestDto);

}
