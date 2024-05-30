package com.viettel.solution.extraction_service.service.impl;

import com.viettel.solution.extraction_service.database.DatabaseConnection;
import com.viettel.solution.extraction_service.dto.RequestDto;
import com.viettel.solution.extraction_service.entity.Database;
import com.viettel.solution.extraction_service.entity.Table;
import com.viettel.solution.extraction_service.repository.TableRepository;
import com.viettel.solution.extraction_service.service.TableService;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.sql.Connection;

@Service
public class TableServiceImpl implements TableService {


    private TableRepository tableRepository;

    @Autowired
    public TableServiceImpl(@Qualifier("tableRepositorySQLImpl") TableRepository tableRepository) {
        this.tableRepository = tableRepository;
    }

    @Override
    public Table getTableStructure(RequestDto requestDto) {
        try {

            String usernameId = requestDto.getUsernameId();
            String type = requestDto.getType();

            SessionFactory sessionFactory = DatabaseConnection.getSessionFactory(usernameId, type);
            if (sessionFactory == null) {
                return null;
            }

            if (type.equalsIgnoreCase("mysql") || type.equalsIgnoreCase("mariadb")) {
                Table table = tableRepository.getTable(sessionFactory, requestDto.getDatabaseName(), requestDto.getSchemaName(), requestDto.getTable());

                return table;
            } else {
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
