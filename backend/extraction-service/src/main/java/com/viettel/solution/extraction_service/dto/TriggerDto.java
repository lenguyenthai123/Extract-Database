package com.viettel.solution.extraction_service.dto;

import com.viettel.solution.extraction_service.entity.Trigger;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TriggerDto {

    private String usernameId;
    private String type;

    @NotBlank(message = "Schema name is required")
    private String schemaName;

    @NotBlank(message = "Table name is required")
    private String tableName;

    private String name;

    private String event;

    private String timing;

    private String doAction;

    private String actionCondition;

    public TriggerDto() {
    }

    public TriggerDto(Trigger trigger) {
        this.name = trigger.getName();
        this.event = trigger.getEvent();
        this.timing = trigger.getTiming();
        this.doAction = trigger.getDoAction();
        this.actionCondition = trigger.getActionCondition();
        this.schemaName = trigger.getSchemaName();
        this.tableName = trigger.getTableName();
    }
}
