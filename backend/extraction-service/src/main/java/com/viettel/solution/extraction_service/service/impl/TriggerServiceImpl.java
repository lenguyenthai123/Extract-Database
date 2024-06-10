package com.viettel.solution.extraction_service.service.impl;

import com.viettel.solution.extraction_service.dao.TriggerDao;
import com.viettel.solution.extraction_service.database.DatabaseConnection;
import com.viettel.solution.extraction_service.dto.TriggerDto;
import com.viettel.solution.extraction_service.entity.Trigger;
import com.viettel.solution.extraction_service.repository.TriggerRepository;
import com.viettel.solution.extraction_service.service.TriggerService;
import org.hibernate.SessionFactory;
import org.hibernate.exception.GenericJDBCException;
import org.hibernate.exception.SQLGrammarException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
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
    @CacheEvict(value = "trigger", key = "#triggerDto.getTableId()")
    public TriggerDto save(String type, String usernameId, TriggerDto triggerDto) {
        try {
            SessionFactory sessionFactory = DatabaseConnection.getSessionFactory(usernameId, type);

            Trigger trigger = new Trigger(triggerDto);

            return new TriggerDto(triggerRepositoryMySql.save(sessionFactory, trigger));
        } catch (GenericJDBCException | SQLGrammarException e) {
            throw e;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    @Cacheable(value = "trigger", key = "#usernameId + #type + #schemaName + #tableName")
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
        } catch (GenericJDBCException | SQLGrammarException e) {
            throw e;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    @CacheEvict(value = "trigger", key = "#triggerDto.getTableId()")
    public TriggerDto update(String type, String usernameId, TriggerDto triggerDto) {
        try {
            SessionFactory sessionFactory = DatabaseConnection.getSessionFactory(usernameId, type);

            Trigger trigger = new Trigger(triggerDto);

            return new TriggerDto(triggerRepositoryMySql.update(sessionFactory, trigger));
        } catch (GenericJDBCException | SQLGrammarException e) {
            throw e;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    @CacheEvict(value = "trigger", key = "#usernameId + #type + #schemaName + #tableName")
    public boolean delete(String type, String usernameId, String schemaName, String tableName, String triggerName) {
        SessionFactory sessionFactory = DatabaseConnection.getSessionFactory(usernameId, type);

        return triggerRepositoryMySql.delete(sessionFactory, schemaName, tableName, triggerName);
    }

}
