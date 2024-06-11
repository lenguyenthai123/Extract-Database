package com.viettel.solution.extraction_service.service.impl;

import com.viettel.solution.extraction_service.database.DatabaseConnection;
import com.viettel.solution.extraction_service.entity.DatabaseConfig;
import com.viettel.solution.extraction_service.service.ConnectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ConnectionServiceImpl implements ConnectionService {


    @Override
    public boolean connect(String usernameId, DatabaseConfig databaseConfigEntity) {
        return DatabaseConnection.createSessionFactory(databaseConfigEntity.getUsernameId(), databaseConfigEntity);

    }

    @Override
    public boolean disconnect(String usernameId, String type) {
        return DatabaseConnection.closeSessionFactory(usernameId, type);
    }
}
