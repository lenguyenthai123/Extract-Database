package com.viettel.solution.extraction_service.service.impl;

import com.viettel.solution.extraction_service.database.DatabaseConnection;
import com.viettel.solution.extraction_service.entity.Table;
import com.viettel.solution.extraction_service.entity.TableDocument;
import com.viettel.solution.extraction_service.repository.TableRepository;
import com.viettel.solution.extraction_service.repository.elasticsearch.TableElasticSearchRepository;
import com.viettel.solution.extraction_service.service.ElasticSearchService;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class ElasticSearchServiceImpl implements ElasticSearchService {

    private TableRepository tableRepository;

    @Autowired
    private TableElasticSearchRepository tableElasticSearchRepository;

    @Autowired
    public ElasticSearchServiceImpl(@Qualifier("tableRepositorySQLImpl") TableRepository tableRepository) {
        this.tableRepository = tableRepository;
    }
    @Autowired
    private RestTemplate restTemplate;

    @Override
    public List<TableDocument> searchTable(String keyword) {
        return null;
    }

    @Override
    public void updateTable(String usernameId, String type, String schemaName, String tableName) {
        try {
            SessionFactory sessionFactory = DatabaseConnection.getSessionFactory(usernameId, type);
            if (sessionFactory == null) {
                return;
            }
            List<TableDocument> tableDocuments = tableElasticSearchRepository.findByUsernameIdAndSchemaNameAndName(usernameId + type, schemaName, tableName);
            for (TableDocument tableDocument : tableDocuments) {
                Table newTable = tableRepository.getTable(sessionFactory, tableDocument.getSchemaName(), null, tableDocument.getName());
                tableDocument.copy(newTable);
                tableElasticSearchRepository.save(tableDocument);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    @Override
//    public void deleteIndex() {
//        String url = "http://localhost:9200/table";
//        restTemplate.delete(url);
//    }

}
