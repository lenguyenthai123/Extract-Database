package com.viettel.solution.extraction_service.service;

// Class này chỉ phục vụ cho việc tìm kiếm dữ liệu của TABLE trong ElasticSearch

import com.viettel.solution.extraction_service.entity.TableDocument;

import java.util.List;

public interface ElasticSearchService {

    public List<TableDocument> searchTable(String keyword);

    public void updateTable(String usernameId, String type, String schemaName, String tableName) ;

    //public void deleteIndex();
}




