package com.viettel.solution.extraction_service.service.impl;

import com.viettel.solution.extraction_service.database.DatabaseConnection;
import com.viettel.solution.extraction_service.dto.RequestDto;
import com.viettel.solution.extraction_service.entity.Database;
import com.viettel.solution.extraction_service.entity.Schema;
import com.viettel.solution.extraction_service.repository.DatabaseRepository;
import com.viettel.solution.extraction_service.repository.SchemaRepository;
import com.viettel.solution.extraction_service.service.DatabaseService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.crypto.Data;
import java.sql.Connection;

@Service
public class DatabaseServiceImpl implements DatabaseService {


    @Autowired
    private DatabaseRepository databaseRepository;

    @Autowired
    private SchemaRepository schemaRepository;

    @Override
    public Database getDatabase(RequestDto requestDto) {
        try {

            String usernameId = requestDto.getUsernameId();
            String type = requestDto.getType();

            Connection connection = DatabaseConnection.getConnection(usernameId, type);
            if (connection == null) {
                return null;
            }

            if (type.equalsIgnoreCase("mysql")) {
                Database database = databaseRepository.getSQLDatabase(connection, requestDto.getDatabaseName());
                return database;
            } else {
                return databaseRepository.getDatabase(connection, requestDto.getDatabaseName());
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}

