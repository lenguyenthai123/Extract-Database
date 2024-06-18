package com.viettel.solution.extraction_service.service.impl;

import com.viettel.solution.extraction_service.database.DatabaseConnection;
import com.viettel.solution.extraction_service.dto.TriggerDto;
import com.viettel.solution.extraction_service.entity.Trigger;
import com.viettel.solution.extraction_service.repository.TriggerRepository;
import com.viettel.solution.extraction_service.service.ElasticSearchService;
import com.viettel.solution.extraction_service.service.TriggerService;
import org.hibernate.SessionFactory;
import org.hibernate.exception.GenericJDBCException;
import org.hibernate.exception.SQLGrammarException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TriggerServiceImpl implements TriggerService {

    @Autowired
    public ElasticSearchService elasticSearchService;

    private TriggerRepository triggerRepositoryMySql;
    private TriggerRepository triggerRepositoryOracle;

    @Autowired
    public TriggerServiceImpl(@Qualifier("triggerRepositorySQLImpl") TriggerRepository triggerRepositoryMySql,
                              @Qualifier("triggerRepositoryOracleImpl") TriggerRepository triggerRepositoryOracle) {
        this.triggerRepositoryMySql = triggerRepositoryMySql;
        this.triggerRepositoryOracle = triggerRepositoryOracle;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "trigger", key = "#triggerDto.getTableId()"),
            @CacheEvict(value = "database", key = "#triggerDto.getDatabaseId()")
    })
    public TriggerDto save(String type, String usernameId, TriggerDto triggerDto) {
        try {
            SessionFactory sessionFactory = DatabaseConnection.getSessionFactory(usernameId, type);

            Trigger trigger = new Trigger(triggerDto);

            TriggerDto result = new TriggerDto(triggerRepositoryMySql.save(sessionFactory, trigger));

            elasticSearchService.updateTable(usernameId, type, triggerDto.getSchemaName(), triggerDto.getTableName());
            return result;

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
    @Caching(evict = {
            @CacheEvict(value = "trigger", key = "#triggerDto.getTableId()"),
            @CacheEvict(value = "database", key = "#triggerDto.getDatabaseId()")
    })
    public TriggerDto update(String type, String usernameId, TriggerDto triggerDto) {
        try {
            SessionFactory sessionFactory = DatabaseConnection.getSessionFactory(usernameId, type);

            Trigger trigger = new Trigger(triggerDto);

            TriggerDto result = new TriggerDto(triggerRepositoryMySql.update(sessionFactory, trigger));

            elasticSearchService.updateTable(usernameId, type, triggerDto.getSchemaName(), triggerDto.getTableName());
            return result;

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
    @Caching(evict = {
            @CacheEvict(value = "trigger", key = "#usernameId + #type + #schemaName + #tableName"),
            @CacheEvict(value = "database", key = "#usernameId + #type")
    })
    public boolean delete(String type, String usernameId, String schemaName, String tableName, String triggerName) {
        SessionFactory sessionFactory = DatabaseConnection.getSessionFactory(usernameId, type);

        boolean result = triggerRepositoryMySql.delete(sessionFactory, schemaName, tableName, triggerName);

        elasticSearchService.updateTable(usernameId, type, schemaName, tableName);
        return result;
    }

}
