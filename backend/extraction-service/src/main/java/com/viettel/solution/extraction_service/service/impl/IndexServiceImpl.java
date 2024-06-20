package com.viettel.solution.extraction_service.service.impl;

import com.viettel.solution.extraction_service.database.DatabaseConnection;
import com.viettel.solution.extraction_service.dto.IndexDto;
import com.viettel.solution.extraction_service.entity.Index;
import com.viettel.solution.extraction_service.repository.IndexRepository;
import com.viettel.solution.extraction_service.service.ElasticSearchService;
import com.viettel.solution.extraction_service.service.IndexService;
import org.hibernate.SessionFactory;
import org.hibernate.exception.GenericJDBCException;
import org.hibernate.exception.SQLGrammarException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class IndexServiceImpl implements IndexService {


    @Autowired
    public ElasticSearchService elasticSearchService;

    private IndexRepository indexRepositoryMySQL;

    @Autowired
    public IndexServiceImpl(@Qualifier("Mysql") IndexRepository indexRepositoryMySQL) {
        this.indexRepositoryMySQL = indexRepositoryMySQL;
    }

    @Override
    @CacheEvict(value = "index", key = "#indexDto.getTableId()")
    public IndexDto save(String type, String usernameId, IndexDto indexDto) {
        try {
            SessionFactory sessionFactory = DatabaseConnection.getSessionFactory(usernameId, type);
            IndexDto result = new IndexDto(indexRepositoryMySQL.save(sessionFactory, new Index(indexDto)));
            elasticSearchService.updateTable(usernameId, type, indexDto.getSchemaName(), indexDto.getTableName());
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
    @Cacheable(value = "index", key = "#usernameId + #type + #schemaName + #tableName")
    public List<IndexDto> getListFromTable(String type, String usernameId, String schemaName, String tableName) {
        try {

            SessionFactory sessionFactory = DatabaseConnection.getSessionFactory(usernameId, type);
            List<Index> indexs = indexRepositoryMySQL.getAllIndexFromTable(sessionFactory, null, schemaName, tableName);

            if (indexs == null) return null;

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
    @CacheEvict(value = "index", key = "#indexDto.getTableId()")
    public IndexDto update(String type, String usernameId, IndexDto indexDto) {
        try {
            SessionFactory sessionFactory = DatabaseConnection.getSessionFactory(usernameId, type);
            IndexDto result = new IndexDto(indexRepositoryMySQL.update(sessionFactory, new Index(indexDto), indexDto.getOldName()));
            elasticSearchService.updateTable(usernameId, type, indexDto.getSchemaName(), indexDto.getTableName());
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
    @CacheEvict(value = "index", key = "#indexDto.getTableId()")
    public boolean delete(String type, String usernameId, IndexDto indexDto) {
        try {
            SessionFactory sessionFactory = DatabaseConnection.getSessionFactory(usernameId, type);
            boolean result = indexRepositoryMySQL.delete(sessionFactory, new Index(indexDto));
            elasticSearchService.updateTable(usernameId, type, indexDto.getSchemaName(), indexDto.getTableName());
            return result;

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
