package com.viettel.solution.extraction_service.repository.elasticsearch;

import com.viettel.solution.extraction_service.entity.TableDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface TableElasticSearchRepository extends ElasticsearchRepository<TableDocument, String> {

    // Phương thức tìm kiếm theo usernameId với phân trang
    Page<TableDocument> findByUsernameId(String usernameId, Pageable pageable);

    // Truy vấn tùy chỉnh theo usernameId và từ khóa
    @Query("{\"bool\": {\"must\": [{\"term\": {\"usernameId\": \"?0\"}}, {\"bool\": {\"should\": [{\"wildcard\": {\"name\": \"*?1*\"}}, {\"wildcard\": {\"description\": \"*?1*\"}}, {\"nested\": {\"path\": \"columns\", \"query\": {\"wildcard\": {\"columns.name\": \"*?1*\"}}}}], \"minimum_should_match\": 1}}]}}")
    List<TableDocument> findByUsernameIdAndKeyword(String usernameId, String keyword);


}