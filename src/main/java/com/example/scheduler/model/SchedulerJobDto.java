package com.example.scheduler.model;

import lombok.Data;

import java.util.HashMap;

@Data
public class SchedulerJobDto {
    private String jobName;
    private String jobGroup;
    private String jobStatus;
    private String jobClass;
    private Boolean cronJob;
    private String cronExpression;
    private String desc;
    private String interfaceName;
    private Long repeatTime;
    private HashMap<String, Object> parameters;
}
