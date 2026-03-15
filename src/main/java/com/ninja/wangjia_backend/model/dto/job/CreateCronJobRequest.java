package com.ninja.wangjia_backend.model.dto.job;

import lombok.Data;
import java.util.Map;

@Data
public class CreateCronJobRequest {
    private String jobClass;
    private String jobName;
    private String groupName = "DEFAULT_GROUP";
    private String cronExpression;
    private Map<String, Object> params;
}
