package com.viettel.solution.extraction_service.config;

import com.viettel.solution.extraction_service.entity.TableDocument;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
public class ElasticSearchConfig {

//    @Autowired
//    private ElasticsearchRestTemplate elasticsearchRestTemplate;
//
//    @PostConstruct
//    public void deleteAllData() {
//        elasticsearchRestTemplate.indexOps(TableDocument.class).delete();
//        elasticsearchRestTemplate.indexOps(TableDocument.class).create();
//        elasticsearchRestTemplate.indexOps(TableDocument.class).putMapping(elasticsearchRestTemplate.indexOps(TableDocument.class).createMapping());
//    }
}