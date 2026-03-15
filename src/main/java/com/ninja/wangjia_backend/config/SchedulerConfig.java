package com.ninja.wangjia_backend.config;

import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory; // <--- 1. 导入这个类

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.concurrent.Executor;

@Configuration
public class SchedulerConfig {

    @Resource
    private DataSource dataSource;

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean() {
        SchedulerFactoryBean factory = new SchedulerFactoryBean();

        // ... 其他配置 (dataSource, quartzProperties 等) ...
        factory.setDataSource(dataSource);
        factory.setSchedulerName("cluster_scheduler");
        // factory.setQuartzProperties(...);

        // ==========================================
        // 【关键修复】设置 JobFactory，让 Spring 管理 Job 的依赖注入
        // ==========================================
        factory.setJobFactory(springBeanJobFactory());

        factory.setTaskExecutor(schedulerThreadPool());
        factory.setAutoStartup(true);
        factory.setWaitForJobsToCompleteOnShutdown(true);

        return factory;
    }

    /**
     * 配置 SpringBeanJobFactory
     * 它的作用是：当 Quartz 需要执行 Job 时，会调用这个 Factory，
     * 从而从 Spring 容器中获取已经注入好依赖的 Job Bean。
     */
    @Bean
    public SpringBeanJobFactory springBeanJobFactory() {
        return new SpringBeanJobFactory();
    }

    @Bean
    public Scheduler scheduler(SchedulerFactoryBean factory) throws Exception {
        return factory.getScheduler();
    }

    @Bean
    public Executor schedulerThreadPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        int cpuCount = Runtime.getRuntime().availableProcessors();
        executor.setCorePoolSize(cpuCount);
        executor.setMaxPoolSize(cpuCount * 2);
        executor.setQueueCapacity(cpuCount * 10);
        executor.setThreadNamePrefix("quartz-job-");
        executor.initialize();
        return executor;
    }
}
