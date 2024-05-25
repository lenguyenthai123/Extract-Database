package com.viettel.solution.extraction_service.service.impl;

import com.viettel.solution.extraction_service.database.DatabaseConnection;
import com.viettel.solution.extraction_service.dto.RequestDto;
import com.viettel.solution.extraction_service.entity.Table;
import com.viettel.solution.extraction_service.repository.TableRepository;
import com.viettel.solution.extraction_service.service.TableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;

@Service
public class TableServiceImpl implements TableService {


    @Autowired
    private TableRepository tableRepository;

    @Override
    public Table getTableStructure(RequestDto requestDto) {
        try {

            Connection connection = DatabaseConnection.getConnection(requestDto.getUsernameId(), requestDto.getType());
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
