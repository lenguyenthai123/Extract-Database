package com.viettel.solution.extraction_service.repository.impl;

import com.viettel.solution.extraction_service.entity.Trigger;
import com.viettel.solution.extraction_service.repository.TriggerRepository;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class TriggerRepositoryImpl implements TriggerRepository {


    @Override
    public Trigger getTrigger(Connection connection, String databaseName, String schemaName, String tableName, String triggerName) {
        String query = "SELECT TRIGGER_NAME, EVENT_MANIPULATION, EVENT_OBJECT_TABLE, ACTION_TIMING, " +
                "ACTION_CONDITION, ACTION_STATEMENT, TRIGGER_SCHEMA " +
                "FROM INFORMATION_SCHEMA.TRIGGERS " +
                "WHERE TRIGGER_SCHEMA = ? AND EVENT_OBJECT_TABLE = ? AND TRIGGER_NAME = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, databaseName);
            stmt.setString(2, tableName);
            stmt.setString(3, triggerName);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Trigger trigger = new Trigger();
                    trigger.setName(rs.getString("TRIGGER_NAME"));
                    trigger.setEvent(rs.getString("EVENT_MANIPULATION"));
                    trigger.setTable(rs.getString("EVENT_OBJECT_TABLE"));
                    trigger.setTiming(rs.getString("ACTION_TIMING"));
                    trigger.setCondition(rs.getString("ACTION_CONDITION"));
                    trigger.setAction(rs.getString("ACTION_STATEMENT"));
                    trigger.setStatus("ENABLED");  // Giả sử trạng thái là ENABLED vì INFORMATION_SCHEMA không cung cấp thông tin này

                    return trigger;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }


    @Override
    public List<Trigger> getAllTrigger(Connection connection, String databaseName, String schemaName, String tableName) {
        List<Trigger> triggers = new ArrayList<>();
        String query = "SELECT TRIGGER_NAME, EVENT_MANIPULATION, EVENT_OBJECT_TABLE, ACTION_TIMING, " +
                "ACTION_CONDITION, ACTION_STATEMENT, TRIGGER_SCHEMA " +
                "FROM INFORMATION_SCHEMA.TRIGGERS " +
                "WHERE TRIGGER_SCHEMA = ? AND EVENT_OBJECT_TABLE = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, databaseName);
            stmt.setString(2, tableName);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Trigger trigger = new Trigger();
                    trigger.setName(rs.getString("TRIGGER_NAME"));
                    trigger.setEvent(rs.getString("EVENT_MANIPULATION"));
                    trigger.setTable(rs.getString("EVENT_OBJECT_TABLE"));
                    trigger.setTiming(rs.getString("ACTION_TIMING"));
                    trigger.setCondition(rs.getString("ACTION_CONDITION"));
                    trigger.setAction(rs.getString("ACTION_STATEMENT"));
                    trigger.setStatus("ENABLED");  // Giả sử trạng thái là ENABLED vì INFORMATION_SCHEMA không cung cấp thông tin này


                    triggers.add(trigger);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return triggers;
    }
}
