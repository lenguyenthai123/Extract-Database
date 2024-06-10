package com.viettel.solution.extraction_service.service;

import com.viettel.solution.extraction_service.dto.TriggerDto;
import com.viettel.solution.extraction_service.entity.Trigger;

import java.util.List;

public interface TriggerService {

    public TriggerDto save(String type, String usernameId, TriggerDto triggerDto);

    public List<TriggerDto> getTriggerListFromTable(String type, String usernameId, String schemaName, String tableName);

    public TriggerDto update(String type, String usernameId, TriggerDto triggerDto);

    public boolean delete(String type, String usernameId, String schemaName, String tableName, String triggerName);
}
