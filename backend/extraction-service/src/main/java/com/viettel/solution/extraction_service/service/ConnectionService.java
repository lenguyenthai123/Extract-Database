package com.viettel.solution.extraction_service.service;

import com.viettel.solution.extraction_service.entity.DatabaseConfig;
import org.springframework.stereotype.Service;

public interface ConnectionService {

    public boolean connect(String usernameId, DatabaseConfig databaseConfigEntity);

    public boolean disconnect(String usernameId, String type);
}
