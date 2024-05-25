package com.viettel.solution.extraction_service.service;

import com.sun.jna.Structure;
import com.viettel.solution.extraction_service.dto.RequestDto;
import com.viettel.solution.extraction_service.entity.DatabaseStructure;
import com.viettel.solution.extraction_service.entity.Table;
import org.hibernate.id.enhanced.TableStructure;

public interface TableService {
    public Table getTableStructure(RequestDto requestDto);

}
