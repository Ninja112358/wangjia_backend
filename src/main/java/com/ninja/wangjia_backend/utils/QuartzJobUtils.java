package com.ninja.wangjia_backend.utils;

import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Date;

@Slf4j
@Component
public class QuartzJobUtils {

    @Resource
    private Scheduler scheduler;

    @PostConstruct
    public void init() {
        if (scheduler == null) {
            throw new IllegalStateException("Scheduler bean is not available!");
        }
    }

    /**
     * 创建/更新 Cron 任务
     * 策略：如果任务已存在，则更新触发时间；如果不存在，则创建。
     * Misfire 策略：重启后补执行一次 (FireAndProceed)
     */
    public boolean createCronJob(Class<? extends Job> jobClass,
                                 String jobName,
                                 String groupName,
                                 String cronExpression,
                                 JobDataMap params) {
        return createJobInternal(jobClass, jobName, groupName, cronExpression, null, -1, params, true);
    }
    public boolean createCronJob(Class<? extends Job> jobClass,
                                 String jobName,
                                 String groupName,
                                 String cronExpression,
                                 JobDataMap params,
                                 boolean replaceIfExists) {
        return createJobInternal(jobClass, jobName, groupName, cronExpression, null, -1, params, replaceIfExists);
    }

    /**
     * 创建/更新 简单间隔任务
     * Misfire 策略：重启后立刻执行 (FireNow)
     */
    public boolean createSimpleJob(Class<? extends Job> jobClass,
                                   String jobName,
                                   String groupName,
                                   int intervalSeconds,
                                   int repeatCount,
                                   JobDataMap params) {
        return createJobInternal(jobClass, jobName, groupName, null, intervalSeconds, repeatCount, params, true);
    }
    public boolean createSimpleJob(Class<? extends Job> jobClass,
                                   String jobName,
                                   String groupName,
                                   int intervalSeconds,
                                   int repeatCount,
                                   JobDataMap params,
                                   boolean replaceIfExists) {
        return createJobInternal(jobClass, jobName, groupName, null, intervalSeconds, repeatCount, params, replaceIfExists);
    }

    /**
     * 【仅创建】如果任务已存在则跳过，不更新
     */
    public boolean createCronJobIfNotExists(Class<? extends Job> jobClass,
                                            String jobName,
                                            String groupName,
                                            String cronExpression,
                                            JobDataMap params) {
        return createJobInternal(jobClass, jobName, groupName, cronExpression, null, -1, params, false);
    }

    /**
     * 核心实现方法
     * @param replaceIfExists true=存在则更新，false=存在则跳过
     */
    private boolean createJobInternal(Class<? extends Job> jobClass,
                                      String jobName,
                                      String groupName,
                                      String cronExpression,
                                      Integer intervalSeconds,
                                      Integer repeatCount,
                                      JobDataMap params,
                                      boolean replaceIfExists) {

        try {
            JobKey jobKey = new JobKey(jobName, groupName);

            // 1. 检查是否存在
            boolean exists = scheduler.checkExists(jobKey);

            if (exists && !replaceIfExists) {
                log.info("⚠️ 任务已存在且设置为不覆盖，跳过：{}.{}", groupName, jobName);
                return false;
            }

            // 2. 构建 JobDetail
            // 注意：如果任务已存在且我们只是想更新 Trigger，JobDetail 可以复用旧的，但这里为了简单统一重建
            JobBuilder jobBuilder = JobBuilder.newJob(jobClass)
                    .withIdentity(jobKey)
                    .storeDurably();

            if (params != null && !params.isEmpty()) {
                jobBuilder.usingJobData(params);
            }
            JobDetail jobDetail = jobBuilder.build();

            // 3. 构建 Trigger
            TriggerKey triggerKey = new TriggerKey(jobName + "_trigger", groupName);
            TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger()
                    .withIdentity(triggerKey)
                    .forJob(jobDetail);
            // 移除 startNow()，让 Quartz 根据 Cron 自动计算下一个时间点，避免不必要的 Misfire 判断干扰

            // 【关键】设置 Misfire 策略
            if (cronExpression != null) {
                triggerBuilder.withSchedule(
                        CronScheduleBuilder.cronSchedule(cronExpression)
                                .withMisfireHandlingInstructionFireAndProceed() // 错过则补执行一次
                );
            } else if (intervalSeconds != null) {
                SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInSeconds(intervalSeconds)
                        .withMisfireHandlingInstructionFireNow(); // 错过则立即执行

                if (repeatCount != null && repeatCount >= 0) {
                    scheduleBuilder.withRepeatCount(repeatCount);
                } else {
                    scheduleBuilder.repeatForever();
                }
                triggerBuilder.withSchedule(scheduleBuilder);
            } else {
                throw new IllegalArgumentException("必须提供 cronExpression 或 intervalSeconds");
            }

            Trigger trigger = triggerBuilder.build();

            // 4. 执行调度
            Date nextFireTime;
            if (exists) {
                // 如果存在，使用 rescheduleJob 专门更新 Trigger，这样更安全，不会触动 JobDetail
                nextFireTime = scheduler.rescheduleJob(triggerKey, trigger);
                log.info("🔄 任务已更新：{}.{}, 下次执行时间：{}", groupName, jobName, nextFireTime);
            } else {
                // 如果不存在，全新注册
                nextFireTime = scheduler.scheduleJob(jobDetail, trigger);
                log.info("✅ 任务已创建：{}.{}, 下次执行时间：{}", groupName, jobName, nextFireTime);
            }

            return true;

        } catch (SchedulerException e) {
            log.error("❌ 操作 Quartz 任务失败：{}.{}", groupName, jobName, e);
            throw new RuntimeException("Failed to schedule job", e);
        }
    }
    /**
     * 创建一次性延迟任务 (只执行一次)
     * <p>
     * 场景：例如订单超时取消、1 小时后发送提醒等。
     * 执行完后任务会自动失效，不会重复执行。
     *
     * @param jobClass      Job 实现类
     * @param jobName       任务名称 (建议唯一，如 "OrderTimeout_" + orderId)
     * @param groupName     任务组名
     * @param delaySeconds  延迟多少秒后执行 (例如 3600 秒 = 1 小时)
     * @param params        传递的参数 (如 orderId, userId 等)
     * @return true 表示成功创建
     */
    public boolean createOneTimeDelayJob(Class<? extends Job> jobClass,
                                         String jobName,
                                         String groupName,
                                         int delaySeconds,
                                         JobDataMap params) {
        // 默认如果存在则覆盖（因为延迟任务通常具有时效性，旧的需要被新的时间覆盖）
        return createOneTimeDelayJobInternal(jobClass, jobName, groupName, delaySeconds, params, true);
    }

    /**
     * 内部核心实现：创建一次性延迟任务
     */
    private boolean createOneTimeDelayJobInternal(Class<? extends Job> jobClass,
                                                  String jobName,
                                                  String groupName,
                                                  int delaySeconds,
                                                  JobDataMap params,
                                                  boolean replaceIfExists) {
        try {
            JobKey jobKey = new JobKey(jobName, groupName);

            // 1. 检查是否存在
            boolean exists = scheduler.checkExists(jobKey);
            if (exists && !replaceIfExists) {
                log.info("⚠️ 一次性任务已存在且设置为不覆盖，跳过：{}.{}", groupName, jobName);
                return false;
            }

            // 2. 构建 JobDetail
            JobBuilder jobBuilder = JobBuilder.newJob(jobClass)
                    .withIdentity(jobKey)
                    .storeDurably(); // 必须持久化，否则重启后丢失

            if (params != null && !params.isEmpty()) {
                jobBuilder.usingJobData(params);
            }
            JobDetail jobDetail = jobBuilder.build();

            // 3. 计算执行时间：当前时间 + 延迟秒数
            Date runTime = new Date(System.currentTimeMillis() + (long) delaySeconds * 1000);

            // 4. 构建 Trigger
            TriggerKey triggerKey = new TriggerKey(jobName + "_trigger", groupName);

            // 【关键】使用 SimpleSchedule，重复次数设为 0 (即只执行 1 次)
            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity(triggerKey)
                    .forJob(jobDetail)
                    .startAt(runTime) // 【关键】指定未来的启动时间
                    .withSchedule(
                            SimpleScheduleBuilder.simpleSchedule()
                                    .withRepeatCount(0) // 【核心】0 表示不重复，总共执行 1 次
                    )
                    .build();

            // 5. 执行调度
            Date nextFireTime;
            if (exists) {
                nextFireTime = scheduler.rescheduleJob(triggerKey, trigger);
                log.info("🔄 一次性任务已更新：{}.{}, 将在 {} 后执行 (时间点：{})",
                        groupName, jobName, delaySeconds, nextFireTime);
            } else {
                nextFireTime = scheduler.scheduleJob(jobDetail, trigger);
                log.info("✅ 一次性任务已创建：{}.{}, 将在 {} 秒后执行 (时间点：{})",
                        groupName, jobName, delaySeconds, nextFireTime);
            }

            return true;

        } catch (SchedulerException e) {
            log.error("❌ 创建一次性延迟任务失败：{}.{}", groupName, jobName, e);
            throw new RuntimeException("Failed to schedule one-time job", e);
        }
    }


    // --- 以下辅助方法保持不变 ---

    public void pauseJob(String jobName, String groupName) {
        try {
            scheduler.pauseJob(new JobKey(jobName, groupName));
            log.info("⏸️ 任务已暂停：{}.{}", groupName, jobName);
        } catch (SchedulerException e) {
            log.error("暂停任务失败", e);
        }
    }

    public void resumeJob(String jobName, String groupName) {
        try {
            scheduler.resumeJob(new JobKey(jobName, groupName));
            log.info("▶️ 任务已恢复：{}.{}", groupName, jobName);
        } catch (SchedulerException e) {
            log.error("恢复任务失败", e);
        }
    }

    public void deleteJob(String jobName, String groupName) {
        try {
            boolean result = scheduler.deleteJob(new JobKey(jobName, groupName));
            if (result) {
                log.info("🗑️ 任务已删除：{}.{}", groupName, jobName);
            } else {
                log.warn("任务不存在，删除失败：{}.{}", groupName, jobName);
            }
        } catch (SchedulerException e) {
            log.error("删除任务失败", e);
        }
    }

    public void triggerNow(String jobName, String groupName) {
        try {
            scheduler.triggerJob(new JobKey(jobName, groupName));
            log.info("⚡ 任务已被手动立即触发：{}.{}", groupName, jobName);
        } catch (SchedulerException e) {
            log.error("立即触发任务失败", e);
        }
    }
    /**
     * 判断 Job 是否存在
     *
     * @param jobName   任务名称
     * @param groupName 任务组名
     * @return true 表示存在，false 表示不存在
     */
    public boolean jobExists(String jobName, String groupName) {
        try {
            JobKey jobKey = new JobKey(jobName, groupName);
            boolean exists = scheduler.checkExists(jobKey);

            if (exists) {
                log.debug("✅ 任务检查：{}.{} 存在", groupName, jobName);
            } else {
                log.debug("❌ 任务检查：{}.{} 不存在", groupName, jobName);
            }
            return exists;
        } catch (SchedulerException e) {
            log.error("检查任务状态失败：{}.{}", groupName, jobName, e);
            // 发生异常时，保守返回 false 或抛出运行时异常，这里选择返回 false 并记录日志
            return false;
        }
    }
}
