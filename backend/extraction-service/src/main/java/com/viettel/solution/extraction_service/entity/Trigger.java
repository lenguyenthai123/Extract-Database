package com.viettel.solution.extraction_service.entity;

import com.viettel.solution.extraction_service.dto.TriggerDto;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;

//@Entity
//@Table(name = "information_schema.TRIGGERS")
@Component
@Data               // Bao gồm @Getter, @Setter, @ToString, @EqualsAndHashCode, và @RequiredArgsConstructor
@AllArgsConstructor // Tạo constructor với tất cả các tham số
@Builder
public class Trigger {

    //@Column(name = "TRIGGER_SCHEMA")
    private String schemaName;

    //@Column(name = "TRIGGER_NAME")
    private String name;

    //@Column(name = "EVENT_MANIPULATION")
    private String event;

    //@Column(name = "EVENT_OBJECT_TABLE")
    private String tableName;

    //@Column(name = "ACTION_TIMING")
    private String timing;

    //@Column(name = "ACTION_STATEMENT")
    private String doAction;

    //@Column(name = "ACTION_CONDITION")
    private String actionCondition;

    public Trigger() {
    }

    public Trigger(TriggerDto triggerDto) {
        this.schemaName = triggerDto.getSchemaName();
        this.name = triggerDto.getName();
        this.event = triggerDto.getEvent();
        this.tableName = triggerDto.getTableName();
        this.timing = triggerDto.getTiming();
        this.doAction = triggerDto.getDoAction();
        this.actionCondition = triggerDto.getActionCondition();
    }
}