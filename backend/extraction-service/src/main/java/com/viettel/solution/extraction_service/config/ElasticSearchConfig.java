package com.viettel.solution.extraction_service.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

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
    private static final String ELASTICSEARCH_URL = "http://elasticsearch:9200/table";

    @Autowired
    private RestTemplate restTemplate;

    @PostConstruct
    public void init() {
        deleteIndex();
        createIndex();
    }

    private void deleteIndex() {
        try {
            restTemplate.delete(ELASTICSEARCH_URL);
            // System.out.println("Index deleted successfully.");
        } catch (Exception e) {
            // System.err.println("Failed to delete index: " + e.getMessage());
        }
    }

    private void createIndex() {
        String indexConfiguration = "{\n" +
                "  \"settings\": {\n" +
                "    \"analysis\": {\n" +
                "      \"analyzer\": {\n" +
                "        \"custom_analyzer\": {\n" +
                "          \"tokenizer\": \"standard\",\n" +
                "          \"filter\": [\"lowercase\"]\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  },\n" +
                "  \"mappings\": {\n" +
                "    \"properties\": {\n" +
                "      \"name\": {\n" +
                "        \"type\": \"text\",\n" +
                "        \"analyzer\": \"custom_analyzer\"\n" +
                "      },\n" +
                "      \"description\": {\n" +
                "        \"type\": \"text\",\n" +
                "        \"analyzer\": \"custom_analyzer\"\n" +
                "      },\n" +
                "      \"columns\": {\n" +
                "        \"properties\": {\n" +
                "          \"name\": {\n" +
                "            \"type\": \"text\",\n" +
                "            \"analyzer\": \"custom_analyzer\"\n" +
                "          }\n" +
                "        }\n" +
                "      },\n" +
                "      \"constraints\": {\n" +
                "        \"properties\": {\n" +
                "          \"name\": {\n" +
                "            \"type\": \"text\",\n" +
                "            \"analyzer\": \"custom_analyzer\"\n" +
                "          }\n" +
                "        }\n" +
                "      },\n" +
                "      \"indexs\": {\n" +
                "        \"properties\": {\n" +
                "          \"name\": {\n" +
                "            \"type\": \"text\",\n" +
                "            \"analyzer\": \"custom_analyzer\"\n" +
                "          }\n" +
                "        }\n" +
                "      },\n" +
                "      \"triggers\": {\n" +
                "        \"properties\": {\n" +
                "          \"name\": {\n" +
                "            \"type\": \"text\",\n" +
                "            \"analyzer\": \"custom_analyzer\"\n" +
                "          }\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}";
        try {
            restTemplate.put(ELASTICSEARCH_URL, indexConfiguration);
            // System.out.println("Index created successfully.");
        } catch (Exception e) {
            // System.err.println("Failed to create index: " + e.getMessage());
        }
    }

}