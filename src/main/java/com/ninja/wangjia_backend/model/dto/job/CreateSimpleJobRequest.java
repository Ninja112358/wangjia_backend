package com.ninja.wangjia_backend.model.dto.job;

import lombok.Data;

@Data
public class CreateSimpleJobRequest {
    private String jobClass;
    private String jobName;
    private String groupName = "DEFAULT_GROUP";
    private Integer intervalSeconds;
    // -1 表示永久重复
    private Integer repeatCount = -1;
}
