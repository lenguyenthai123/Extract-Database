package com.viettel.solution.extraction_service.service.impl;

import com.viettel.solution.extraction_service.entity.Column;
import com.viettel.solution.extraction_service.entity.DatabaseConfig;
import com.viettel.solution.extraction_service.entity.DatabaseStructure;
import com.viettel.solution.extraction_service.entity.Table;
import com.viettel.solution.extraction_service.repository.DatabaseRepository;
import com.viettel.solution.extraction_service.service.ExtractionService;
import org.springframework.stereotype.Service;

@Service
public class ExtractionServiceImpl implements ExtractionService {


    @Override
    public DatabaseStructure getDatabaseStructure(DatabaseConfig databaseConfigEntity) {
        DatabaseRepository databaseRepository = DatabaseRepository.getPrototype(databaseConfigEntity.getType());
        return databaseRepository.getDatabaseStructure(databaseConfigEntity);
    }

    @Override
    public Table getTableStructure(DatabaseConfig databaseConfigEntity, String tableName) {
        DatabaseRepository databaseRepository = DatabaseRepository.getPrototype(databaseConfigEntity.getType());
        return databaseRepository.getTableStructure(databaseConfigEntity, tableName);
    }

    @Override
    public Column getTableStructure(DatabaseConfig databaseConfigEntity, String tableName, String columnName) {
        DatabaseRepository databaseRepository = DatabaseRepository.getPrototype(databaseConfigEntity.getType());
        return databaseRepository.getColumnStructure(databaseConfigEntity, tableName, columnName);
    }

}

