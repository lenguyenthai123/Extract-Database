package com.viettel.solution.extraction_service.controller;

import com.viettel.solution.extraction_service.exception.CustomException;
import org.hibernate.exception.GenericJDBCException;
import org.hibernate.exception.SQLGrammarException;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class ExceptionController {

    @ExceptionHandler(ArithmeticException.class)
    public String handleArithmeticException(ArithmeticException e) {
        return "arithmeticError";
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(GenericJDBCException.class)
    public ResponseEntity<Map<String, Object>> handleGenericJDBCException(GenericJDBCException ex) {
        Map<String, Object> response = new HashMap<>();

        // Trích xuất các thông tin cần thiết từ ngoại lệ
        String message = ex.getMessage();
        String sqlState = ex.getSQLState();
        Throwable cause = ex.getCause();

        // Tạo thông tin phản hồi cho người dùng
        response.put("error", "Database error");
        response.put("message", message);
        response.put("sqlState", sqlState);

        if (cause != null) {
            response.put("cause", cause.getMessage());
        }

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Xử lý ngoại lệ SQLException từ JDBC
    @ExceptionHandler(SQLException.class)
    public ResponseEntity<Map<String, Object>> handleSQLException(SQLException ex) {
        Map<String, Object> response = new HashMap<>();

        response.put("error", "SQL error");
        response.put("message", ex.getMessage());
        response.put("sqlState", ex.getSQLState());
        response.put("errorCode", ex.getErrorCode());

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Xử lý ngoại lệ SQLGrammarException từ Hibernate
    @ExceptionHandler(SQLGrammarException.class)
    public ResponseEntity<Map<String, Object>> handleSQLGrammarException(SQLGrammarException ex) {
        Map<String, Object> response = new HashMap<>();

        response.put("error", "SQL grammar error");
        response.put("message", ex.getMessage());
        response.put("sql", ex.getSQL());

        Throwable cause = ex.getCause();
        if (cause != null) {
            response.put("cause", cause.getMessage());
        }

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // Xử lý ngoại lệ chung DataAccessException từ Spring Data
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<Map<String, Object>> handleDataAccessException(DataAccessException ex) {
        Map<String, Object> response = new HashMap<>();

        response.put("error", "Data access error");
        response.put("message", ex.getMessage());

        Throwable cause = ex.getCause();
        if (cause != null) {
            response.put("cause", cause.getMessage());
        }

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidOrderException(RuntimeException ex) {
        Map<String, Object> response = new HashMap<>();

        response.put("error", "Data access error");
        response.put("message", ex.getMessage());

        Throwable cause = ex.getCause();
        if (cause != null) {
            response.put("cause", cause.getMessage());
        }
         return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Xử lý ngoại lệ chung
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        Map<String, Object> response = new HashMap<>();

        response.put("error", "Internal server error");
        response.put("message", ex.getMessage());

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
