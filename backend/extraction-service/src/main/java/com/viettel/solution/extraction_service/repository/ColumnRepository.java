package com.viettel.solution.extraction_service.repository;

import com.viettel.solution.extraction_service.dto.ColumnDto;
import com.viettel.solution.extraction_service.dto.RequestDto;
import com.viettel.solution.extraction_service.entity.Column;
import org.hibernate.SessionFactory;

public interface ColumnRepository {

    public Column getColumn(SessionFactory sessionFactory, RequestDto requestDto);

    public Column updateColumn(SessionFactory sessionFactory, ColumnDto columnDto);

    public boolean deleteColumn(SessionFactory sessionFactory, ColumnDto columnDto);

    public boolean addColumn(SessionFactory sessionFactory, ColumnDto columnDto);
}
