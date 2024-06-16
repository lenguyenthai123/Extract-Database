package com.viettel.solution.extraction_service.service.impl;

import com.viettel.solution.extraction_service.database.DatabaseConnection;
import com.viettel.solution.extraction_service.entity.DatabaseConfig;
import com.viettel.solution.extraction_service.entity.Table;
import com.viettel.solution.extraction_service.entity.TableDocument;
import com.viettel.solution.extraction_service.repository.TableRepository;
import com.viettel.solution.extraction_service.repository.elasticsearch.TableElasticSearchRepository;
import com.viettel.solution.extraction_service.service.ConnectionService;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ConnectionServiceImpl implements ConnectionService {


    private TableRepository tableRepository;

    @Autowired
    private TableElasticSearchRepository tableElasticSearchRepository;

    @Autowired
    public ConnectionServiceImpl(@Qualifier("tableRepositorySQLImpl") TableRepository tableRepository) {
        this.tableRepository = tableRepository;
    }


    @Override
    public boolean connect(String usernameId1, DatabaseConfig databaseConfigEntity) {

        try {
            String usernameId = databaseConfigEntity.getUsernameId();
            String type = databaseConfigEntity.getType();

            if (DatabaseConnection.checkConnection(usernameId, type)) {
                return true;
            } else {
                DatabaseConnection.createSessionFactory(databaseConfigEntity.getUsernameId(), databaseConfigEntity);

                SessionFactory sessionFactory = DatabaseConnection.getSessionFactory(usernameId, type);

                // This is domain not repository part

                List<Table> allTables = tableRepository.getAllTableFromDatabase(sessionFactory);

                List<TableDocument> tableDocuments = new ArrayList<>();
                for (Table table : allTables) {
                    TableDocument tableDocument = new TableDocument(table);
                    tableDocument.setUsernameId(usernameId);
                    tableDocuments.add(tableDocument);
                }

                tableElasticSearchRepository.saveAll(tableDocuments);

                // Print
                for(TableDocument tableDocument: tableDocuments)
                {
                    System.out.println(tableDocuments);
                }

                return true;
            }

        }
        catch(RuntimeException e)
        {
            e.printStackTrace();
            throw e;
        }

    }

    @Override
    public boolean disconnect(String usernameId, String type) {
        return DatabaseConnection.closeSessionFactory(usernameId, type);
    }
}
