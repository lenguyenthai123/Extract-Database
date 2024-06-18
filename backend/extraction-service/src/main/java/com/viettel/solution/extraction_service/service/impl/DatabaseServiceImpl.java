package com.viettel.solution.extraction_service.service.impl;

import com.viettel.solution.extraction_service.database.DatabaseConnection;
import com.viettel.solution.extraction_service.dto.RequestDto;
import com.viettel.solution.extraction_service.entity.Database;
import com.viettel.solution.extraction_service.repository.DatabaseRepository;
import com.viettel.solution.extraction_service.repository.SchemaRepository;
import com.viettel.solution.extraction_service.service.DatabaseService;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class DatabaseServiceImpl implements DatabaseService {


    @Autowired
    private SchemaRepository schemaRepository;

    private DatabaseRepository databaseRepositorySQL; // For MariabDB too!!!
    private DatabaseRepository databaseRepositoryOracle;

    @Autowired
    public DatabaseServiceImpl(@Qualifier("databaseRepositorySQLImpl") DatabaseRepository databaseRepositorySQL,
                               @Qualifier("databaseRepositoryOracleImpl") DatabaseRepository databaseRepositorOracleImpl) {
        this.databaseRepositorySQL = databaseRepositorySQL;
        this.databaseRepositoryOracle = databaseRepositorOracleImpl;
    }


    @Override
    @Cacheable(value = "database", key = "#requestDto.getDatabaseId()")
    public Database getDatabase(RequestDto requestDto) {
        try {

            String usernameId = requestDto.getUsernameId();
            String type = requestDto.getType();

            SessionFactory sessionFactory = DatabaseConnection.getSessionFactory(usernameId, type);
            if (sessionFactory == null) {
                return null;
            }

            if (type.equalsIgnoreCase("mysql") || type.equalsIgnoreCase("mariadb")) {
                Database database = databaseRepositorySQL.getDatabase(sessionFactory);

                return database;
            } else {
                return databaseRepositoryOracle.getDatabase(sessionFactory);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}

