package com.viettel.solution.extraction_service.service;

import com.viettel.solution.extraction_service.dto.TriggerDto;
import com.viettel.solution.extraction_service.entity.Trigger;

import java.util.List;

public interface TriggerService {

    public boolean save(String type, String usernameId, TriggerDto triggerDto);

    public TriggerDto findBySchemaTableTriggerName(String type, String usernameId, String schemaName, String tableName, String triggerName);

    public List<TriggerDto> getTriggerListFromTable(String type, String usernameId, String schemaName, String tableName);

}
