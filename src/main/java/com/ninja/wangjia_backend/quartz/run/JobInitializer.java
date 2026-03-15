package com.ninja.wangjia_backend.quartz.run;

import com.ninja.wangjia_backend.constant.JobConstant;
import com.ninja.wangjia_backend.quartz.jobs.DalyDeductJob;
import com.ninja.wangjia_backend.quartz.jobs.TestJob;
import com.ninja.wangjia_backend.utils.QuartzJobUtils;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDataMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;

@Slf4j
@Component
public class JobInitializer implements CommandLineRunner {

    @Resource
    private QuartzJobUtils quartzJobUtils;

    @Override
    public void run(String... args) throws Exception {
        log.info("🔧 开始初始化 Quartz 定时任务...");

        // 准备参数 (可选)
        JobDataMap params = new JobDataMap();
        params.put("name", "Ninja");
        //quartzJobUtils.deleteJob(JobConstant.DALY_JOB_NAME, JobConstant.DALY_JOB_GROUP);
        boolean success = quartzJobUtils.createCronJob(
                DalyDeductJob.class,
                JobConstant.DALY_JOB_NAME,       // 任务名
                JobConstant.DALY_JOB_GROUP,         // 组名
                "0 30 14 * * ?",          // 【核心】每天 14:30
                params,
                true
        );
        if (success) {
            log.info("☀️ 定时任务 [每日 14:30] 注册成功！等待执行...");
        } else {
            log.info("⚠️ 定时任务 [每日 14:30] 已存在，跳过注册。");
        }

    }
}
