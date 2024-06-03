package com.viettel.solution.extraction_service.service.impl;

import com.viettel.solution.extraction_service.dao.TriggerDao;
import com.viettel.solution.extraction_service.database.DatabaseConnection;
import com.viettel.solution.extraction_service.dto.TriggerDto;
import com.viettel.solution.extraction_service.entity.Trigger;
import com.viettel.solution.extraction_service.repository.TriggerRepository;
import com.viettel.solution.extraction_service.service.TriggerService;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.viettel.solution.extraction_service.service.TriggerService;

import java.util.ArrayList;
import java.util.List;

@Service
public class TriggerServiceImpl implements TriggerService {


    private TriggerRepository triggerRepositoryMySql;
    private TriggerRepository triggerRepositoryOracle;

    @Autowired
    public TriggerServiceImpl(@Qualifier("triggerRepositorySQLImpl") TriggerRepository triggerRepositoryMySql,
                              @Qualifier("triggerRepositoryOracleImpl") TriggerRepository triggerRepositoryOracle) {
        this.triggerRepositoryMySql = triggerRepositoryMySql;
        this.triggerRepositoryOracle = triggerRepositoryOracle;
    }

    @Override
    public boolean save(String type, String usernameId, TriggerDto triggerDto) {
        SessionFactory sessionFactory = DatabaseConnection.getSessionFactory(usernameId, type);

        Trigger trigger = new Trigger(triggerDto);

        return triggerRepositoryMySql.save(sessionFactory, trigger);

    }

    @Override
    public TriggerDto findBySchemaTableTriggerName(String type, String usernameId, String schemaName, String tableName, String triggerName) {
        SessionFactory sessionFactory = DatabaseConnection.getSessionFactory(usernameId, type);

        // return new TriggerDto(triggerDao.findBySchemaTableTriggerName(sessionFactory, schemaName, tableName, triggerName));
        return null;
    }

    @Override
    public List<TriggerDto> getTriggerListFromTable(String type, String usernameId, String schemaName, String tableName) {
        try {

            SessionFactory sessionFactory = DatabaseConnection.getSessionFactory(usernameId, type);
            List<Trigger> triggers = triggerRepositoryMySql.getTriggerListFromTable(sessionFactory, null, schemaName, tableName);

            List<TriggerDto> triggerDtos = new ArrayList<>();
            for (int i = 0; i < triggers.size(); i++) {
                Trigger trigger = triggers.get(i);
                triggerDtos.add(new TriggerDto(triggers.get(i)));
            }
            return triggerDtos;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
