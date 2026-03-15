package com.ninja.wangjia_backend.quartz.jobs;

import lombok.Data;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.Date;

@Data
@PersistJobDataAfterExecution   // 允许任务数据jobData被持久化
@DisallowConcurrentExecution    // 禁止并发执行
public class TestJob extends QuartzJobBean {
    private String name;
    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        System.out.println(name + ":" + new Date());

    }
}
