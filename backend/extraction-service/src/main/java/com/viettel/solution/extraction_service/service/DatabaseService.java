package com.viettel.solution.extraction_service.service;

import com.viettel.solution.extraction_service.dto.RequestDto;
import com.viettel.solution.extraction_service.entity.Column;
import com.viettel.solution.extraction_service.entity.DatabaseConfig;
import com.viettel.solution.extraction_service.entity.DatabaseStructure;
import com.viettel.solution.extraction_service.entity.Table;

public interface DatabaseService {

    public DatabaseStructure getDatabaseStructure(RequestDto requestDto);


}
