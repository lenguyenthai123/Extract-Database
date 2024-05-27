package com.viettel.solution.extraction_service.service.impl;

import com.viettel.solution.extraction_service.database.DatabaseConnection;
import com.viettel.solution.extraction_service.dto.RequestDto;
import com.viettel.solution.extraction_service.entity.Table;
import com.viettel.solution.extraction_service.service.SchemaService;
import org.springframework.stereotype.Service;

import java.sql.Connection;

@Service
public class SchemaServiceImpl implements SchemaService {
    @Override
    public Table getSchemaStructure(RequestDto requestDto) {
        try {
            Connection connection = null ;//DatabaseConnection.getConnection(requestDto.getUsernameId(), requestDto.getType());
            if (connection == null) {
                return null;
            }
            //return tableRepository.getTableStructure(connection, requestDto.getDatabaseName());
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
