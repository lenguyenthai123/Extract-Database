package com.viettel.solution.extraction_service.service.impl;

import com.viettel.solution.extraction_service.database.DatabaseConnection;
import com.viettel.solution.extraction_service.dto.RequestDto;
import com.viettel.solution.extraction_service.entity.DatabaseStructure;
import com.viettel.solution.extraction_service.repository.DatabaseRepository;
import com.viettel.solution.extraction_service.service.DatabaseService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;

@Service
public class DatabaseServiceImpl implements DatabaseService {


    @Autowired
    private DatabaseRepository databaseRepository;

    @Override
    public DatabaseStructure getDatabaseStructure(RequestDto requestDto) {
        try {

            Connection connection = DatabaseConnection.getConnection(requestDto.getUsernameId(), requestDto.getType());
            if (connection == null) {
                return null;
            }
            return databaseRepository.getDatabaseStructure(connection, requestDto.getDatabaseName());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}

