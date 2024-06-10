package com.viettel.solution.extraction_service.service.impl;


import com.viettel.solution.extraction_service.database.DatabaseConnection;
import com.viettel.solution.extraction_service.dto.ColumnDto;
import com.viettel.solution.extraction_service.dto.RequestDto;
import com.viettel.solution.extraction_service.entity.Column;
import com.viettel.solution.extraction_service.entity.Database;
import com.viettel.solution.extraction_service.repository.ColumnRepository;
import com.viettel.solution.extraction_service.service.ColumnService;
import org.hibernate.SessionFactory;
import org.hibernate.exception.GenericJDBCException;
import org.hibernate.exception.SQLGrammarException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ColumnServiceImpl implements ColumnService {


    private ColumnRepository columnRepositorySQL; // For MariabDB too!!!

    @Autowired
    public ColumnServiceImpl(@Qualifier("MySql") ColumnRepository columnRepositorySQL) {
        this.columnRepositorySQL = columnRepositorySQL;
    }


    @Override
    public ColumnDto getColumn(RequestDto requestDto) {
        try {

            String usernameId = requestDto.getUsernameId();
            String type = requestDto.getType();

            SessionFactory sessionFactory = DatabaseConnection.getSessionFactory(usernameId, type);
            if (sessionFactory == null) {
                return null;
            }

            if (type.equalsIgnoreCase("mysql") || type.equalsIgnoreCase("mariadb")) {
                Column column = columnRepositorySQL.getColumn(sessionFactory, requestDto);

                return new ColumnDto(column);
            } else {

                return null;
            }

        } catch (GenericJDBCException | SQLGrammarException e) {

            throw e;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    @Cacheable(value = "column", key = "#requestDto.getTableId()")
    public List<ColumnDto> getAllColumn(RequestDto requestDto) {
        try {
            System.out.println("Get all column" + requestDto.toString());
            String usernameId = requestDto.getUsernameId();
            String type = requestDto.getType();

            SessionFactory sessionFactory = DatabaseConnection.getSessionFactory(usernameId, type);
            if (sessionFactory == null) {
                return null;
            }

            if (type.equalsIgnoreCase("mysql") || type.equalsIgnoreCase("mariadb")) {
                List<Column> columns = columnRepositorySQL.getAllColumn(sessionFactory, requestDto);

                if (columns == null) return null;

                return ColumnDto.convertList(columns);
            } else {

                return null;
            }

        } catch (GenericJDBCException | SQLGrammarException e) {

            throw e;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    @Override
    @CacheEvict(value = "column", key = "#column.getTableId()")
    public ColumnDto addColumn(ColumnDto column) {
        try {

            String usernameId = column.getUsernameId();
            String type = column.getType();

            SessionFactory sessionFactory = DatabaseConnection.getSessionFactory(usernameId, type);
            if (sessionFactory == null) {
                return null;
            }

            if (type.equalsIgnoreCase("mysql") || type.equalsIgnoreCase("mariadb")) {
                return new ColumnDto(columnRepositorySQL.addColumn(sessionFactory, column));

            } else {
                return null;
            }
        } catch (GenericJDBCException | SQLGrammarException e) {

            throw e;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    @Override
    @CacheEvict(value = "column", key = "#column.getTableId()")
    public ColumnDto updateColumn(ColumnDto column) {
        try {

            String usernameId = column.getUsernameId();
            String type = column.getType();

            SessionFactory sessionFactory = DatabaseConnection.getSessionFactory(usernameId, type);
            if (sessionFactory == null) {
                return null;
            }

            if (type.equalsIgnoreCase("mysql") || type.equalsIgnoreCase("mariadb")) {
                return new ColumnDto(columnRepositorySQL.updateColumn(sessionFactory, column));

            } else {
                return null;
            }

        } catch (GenericJDBCException | SQLGrammarException e) {

            throw e;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    @CacheEvict(value = "column", key = "#column.getTableId()")
    public boolean deleteColumn(ColumnDto column) {
        try {

            String usernameId = column.getUsernameId();
            String type = column.getType();

            SessionFactory sessionFactory = DatabaseConnection.getSessionFactory(usernameId, type);
            if (sessionFactory == null) {
                return false;
            }

            if (type.equalsIgnoreCase("mysql") || type.equalsIgnoreCase("mariadb")) {
                return columnRepositorySQL.deleteColumn(sessionFactory, column);

            } else {
                return false;
            }

        } catch (GenericJDBCException | SQLGrammarException e) {
            throw e;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


}
