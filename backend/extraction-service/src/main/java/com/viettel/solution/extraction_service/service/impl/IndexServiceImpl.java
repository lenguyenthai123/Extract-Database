package com.viettel.solution.extraction_service.service.impl;

import com.viettel.solution.extraction_service.database.DatabaseConnection;
import com.viettel.solution.extraction_service.dto.IndexDto;
import com.viettel.solution.extraction_service.dto.TriggerDto;
import com.viettel.solution.extraction_service.entity.Index;
import com.viettel.solution.extraction_service.entity.Trigger;
import com.viettel.solution.extraction_service.repository.IndexRepository;
import com.viettel.solution.extraction_service.service.IndexService;
import org.hibernate.SessionFactory;
import org.hibernate.exception.GenericJDBCException;
import org.hibernate.exception.SQLGrammarException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class IndexServiceImpl implements IndexService {


    private IndexRepository indexRepositoryMySQL;

    @Autowired
    public IndexServiceImpl(@Qualifier("Mysql") IndexRepository indexRepositoryMySQL) {
        this.indexRepositoryMySQL = indexRepositoryMySQL;
    }

    @Override
    public boolean save(String type, String usernameId, IndexDto indexDto) {
        try {
            SessionFactory sessionFactory = DatabaseConnection.getSessionFactory(usernameId, type);
            return indexRepositoryMySQL.save(sessionFactory, new Index(indexDto));

        } catch (GenericJDBCException | SQLGrammarException e) {
            throw e;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<IndexDto> getListFromTable(String type, String usernameId, String schemaName, String tableName) {
        try {

            SessionFactory sessionFactory = DatabaseConnection.getSessionFactory(usernameId, type);
            List<Index> indexs = indexRepositoryMySQL.getAllIndexFromTable(sessionFactory, null, schemaName, tableName);

            List<IndexDto> indexDtos = new ArrayList<>();
            for (int i = 0; i < indexs.size(); i++) {
                Index index = indexs.get(i);
                indexDtos.add(new IndexDto(index));
            }
            return indexDtos;
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
    public boolean update(String type, String usernameId, IndexDto indexDto) {
        try {
            SessionFactory sessionFactory = DatabaseConnection.getSessionFactory(usernameId, type);
            return indexRepositoryMySQL.update(sessionFactory, new Index(indexDto), indexDto.getOldName());
        } catch (GenericJDBCException | SQLGrammarException e) {
            throw e;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    @Override
    public boolean delete(String type, String usernameId, IndexDto indexDto) {
        try {
            SessionFactory sessionFactory = DatabaseConnection.getSessionFactory(usernameId, type);
            return indexRepositoryMySQL.delete(sessionFactory, new Index(indexDto));
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
