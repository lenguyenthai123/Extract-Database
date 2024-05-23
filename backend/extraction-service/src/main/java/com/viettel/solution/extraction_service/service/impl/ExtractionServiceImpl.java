package com.viettel.solution.extraction_service.service.impl;

import com.viettel.solution.extraction_service.entity.DatabaseConfigEntity;
import com.viettel.solution.extraction_service.entity.DatabaseEntity;
import com.viettel.solution.extraction_service.repository.DatabaseRepository;
import com.viettel.solution.extraction_service.service.ExtractionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ExtractionServiceImpl implements ExtractionService {


    @Override
    public DatabaseEntity getDatabaseStructure(DatabaseConfigEntity databaseConfigEntity) {
        DatabaseRepository databaseRepository = DatabaseRepository.getPrototype(databaseConfigEntity.getType());
        return databaseRepository.getDatabaseStructure(databaseConfigEntity);
    }

}

