package com.viettel.solution.extraction_service.repository;

import com.viettel.solution.extraction_service.entity.Database;
import org.hibernate.SessionFactory;

import java.sql.Connection;
import java.util.List;

public interface DatabaseRepository {

    public Database getDatabase(SessionFactory sessionFactory);

}
