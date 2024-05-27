package com.viettel.solution.extraction_service.entity;

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

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Trigger {


    @Column(name = "NAME")
    private String name;

    @Column(name = "EVENT")
    private String event;

    @Column(name = "TABLE_NAME")
    private String tableName;

    @Column(name = "TIMING")
    private String timing;

    @Column(name = "DO_ACTION")
    private String doAction;

    @Column(name = "ACTION_CONDITION")
    private String actionCondition;
}