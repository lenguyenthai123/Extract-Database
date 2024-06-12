package com.viettel.solution.extraction_service.service.impl;

import com.viettel.solution.extraction_service.database.DatabaseConnection;
import com.viettel.solution.extraction_service.dto.ConstraintDto;
import com.viettel.solution.extraction_service.dto.IndexDto;
import com.viettel.solution.extraction_service.entity.Constraint;
import com.viettel.solution.extraction_service.entity.Index;
import com.viettel.solution.extraction_service.repository.ConstraintRepository;
import com.viettel.solution.extraction_service.service.ConstraintService;
import org.hibernate.SessionFactory;
import org.hibernate.exception.GenericJDBCException;
import org.hibernate.exception.SQLGrammarException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class ConstraintServiceImpl implements ConstraintService {


    private ConstraintRepository constraintRepositoryMySql;
    private ConstraintRepository constraintRepositoryOracle;

    @Autowired
    public ConstraintServiceImpl(@Qualifier("Mysql") ConstraintRepository constraintRepositoryMySql) {
        this.constraintRepositoryMySql = constraintRepositoryMySql;
    }

    @Override
    @CacheEvict(value = "constraint", key = "#constraintDto.getTableId()")
    public ConstraintDto save(String type, String usernameId, ConstraintDto constraintDto) {
        try {
            SessionFactory sessionFactory = DatabaseConnection.getSessionFactory(usernameId, type);
            return new ConstraintDto(constraintRepositoryMySql.save(sessionFactory, new Constraint(constraintDto)));

        } catch (GenericJDBCException | SQLGrammarException e) {
            throw e;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

    }

    @Override
    @Cacheable(value = "constraint", key = "#usernameId + #type + #schemaName + #tableName")
    public List<ConstraintDto> getListFromTable(String type, String usernameId, String schemaName, String tableName) {
        try {

            SessionFactory sessionFactory = DatabaseConnection.getSessionFactory(usernameId, type);
            List<Constraint> constraints = constraintRepositoryMySql.getAllConstraintFromTable(sessionFactory, null, schemaName, tableName);

            if (constraints == null) return null;

            List<ConstraintDto> constraintDtos = new ArrayList<>();

            for (int i = 0; i < constraints.size(); i++) {
                Constraint constraint = constraints.get(i);
                constraintDtos.add(new ConstraintDto(constraint));
            }
            return constraintDtos;
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
    @CacheEvict(value = "constraint", key = "#constraintDto.getTableId()")
    public ConstraintDto update(String type, String usernameId, ConstraintDto constraintDto) {
        try {
            SessionFactory sessionFactory = DatabaseConnection.getSessionFactory(usernameId, type);
            return new ConstraintDto(constraintRepositoryMySql.update(sessionFactory, new Constraint(constraintDto), constraintDto.getOldName()));
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
    @CacheEvict(value = "constraint", key = "#constraintDto.getTableId()")
    public boolean delete(String type, String usernameId, ConstraintDto constraintDto) {
        try {
            SessionFactory sessionFactory = DatabaseConnection.getSessionFactory(usernameId, type);
            return constraintRepositoryMySql.delete(sessionFactory, new Constraint(constraintDto));
        } catch (GenericJDBCException | SQLGrammarException e) {
            throw e;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
