package com.viettel.solution.extraction_service.service;

import com.viettel.solution.extraction_service.dto.RequestDto;
import com.viettel.solution.extraction_service.entity.Database;

public interface DatabaseService {

    public Database getDatabase(RequestDto requestDto);


}
