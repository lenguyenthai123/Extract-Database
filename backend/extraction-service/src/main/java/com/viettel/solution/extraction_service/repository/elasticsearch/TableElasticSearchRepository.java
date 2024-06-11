package com.viettel.solution.extraction_service.repository.elasticsearch;

import com.viettel.solution.extraction_service.entity.TableDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface TableElasticSearchRepository extends ElasticsearchRepository<TableDocument, String> {
    List<TableDocument> findByUsernameId(String usernameId);
}
