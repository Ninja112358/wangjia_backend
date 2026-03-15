package com.ninja.wangjia_backend.controller;

import com.ninja.wangjia_backend.annotation.AuthCheck;
import com.ninja.wangjia_backend.model.dto.job.CreateCronJobRequest;
import com.ninja.wangjia_backend.model.dto.job.CreateSimpleJobRequest;
import com.ninja.wangjia_backend.utils.QuartzJobUtils;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/api/quartz")
public class JobController {

    @Autowired
    private QuartzJobUtils quartzJobUtils;

    @Autowired
    private Scheduler scheduler;

    /**
     * 1. 创建或更新一个 Cron 任务
     * URL: POST /api/quartz/cron
     */
    @PostMapping("/cron")
    @AuthCheck(mustRole = "admin")
    public Map<String, Object> createCronJob(@RequestBody @Validated CreateCronJobRequest req) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 反射获取 Class
            Class<? extends Job> jobClass = (Class<? extends Job>) Class.forName(req.getJobClass());

            // 转换参数
            JobDataMap params = new JobDataMap();
            if (req.getParams() != null) {
                req.getParams().forEach(params::put);
            }

            boolean success = quartzJobUtils.createCronJob(
                    jobClass,
                    req.getJobName(),
                    req.getGroupName(),
                    req.getCronExpression(),
                    params
            );

            result.put("success", success);
            result.put("message", success ? "任务创建/更新成功" : "任务已存在且设置为不覆盖");
            return result;

        } catch (ClassNotFoundException e) {
            log.error("找不到 Job 类：{}", req.getJobClass(), e);
            result.put("success", false);
            result.put("message", "找不到指定的 Job 类：" + req.getJobClass());
            return result;
        } catch (Exception e) {
            log.error("创建任务失败", e);
            result.put("success", false);
            result.put("message", "创建失败：" + e.getMessage());
            return result;
        }
    }

    /**
     * 2. 创建或更新一个简单间隔任务
     * URL: POST /api/quartz/simple
     */
    @PostMapping("/simple")
    @AuthCheck(mustRole = "admin")
    public Map<String, Object> createSimpleJob(@RequestBody @Validated CreateSimpleJobRequest req) {
        Map<String, Object> result = new HashMap<>();
        try {
            Class<? extends Job> jobClass = (Class<? extends Job>) Class.forName(req.getJobClass());

            boolean success = quartzJobUtils.createSimpleJob(
                    jobClass,
                    req.getJobName(),
                    req.getGroupName(),
                    req.getIntervalSeconds(),
                    req.getRepeatCount(),
                    null
            );

            result.put("success", success);
            result.put("message", success ? "简单任务创建成功" : "任务已存在");
            return result;

        } catch (ClassNotFoundException e) {
            log.error("找不到 Job 类：{}", req.getJobClass(), e);
            result.put("success", false);
            result.put("message", "找不到指定的 Job 类：" + req.getJobClass());
            return result;
        } catch (Exception e) {
            log.error("创建简单任务失败", e);
            result.put("success", false);
            result.put("message", "创建失败：" + e.getMessage());
            return result;
        }
    }

    /**
     * 3. 暂停任务
     * URL: POST /api/quartz/pause/{jobName}?groupName=DEFAULT_GROUP
     */
    @PostMapping("/pause/{jobName}")
    @AuthCheck(mustRole = "admin")
    public Map<String, Object> pauseJob(@PathVariable String jobName,
                                        @RequestParam(defaultValue = "DEFAULT_GROUP") String groupName) {
        return executeAndWrap(() -> {
            quartzJobUtils.pauseJob(jobName, groupName);
            return "任务已暂停";
        });
    }

    /**
     * 4. 恢复任务
     * URL: POST /api/quartz/resume/{jobName}?groupName=DEFAULT_GROUP
     */
    @PostMapping("/resume/{jobName}")
    @AuthCheck(mustRole = "admin")
    public Map<String, Object> resumeJob(@PathVariable String jobName,
                                         @RequestParam(defaultValue = "DEFAULT_GROUP") String groupName) {
        return executeAndWrap(() -> {
            quartzJobUtils.resumeJob(jobName, groupName);
            return "任务已恢复";
        });
    }

    /**
     * 5. 立即触发一次任务
     * URL: POST /api/quartz/trigger/{jobName}?groupName=DEFAULT_GROUP
     */
    @PostMapping("/trigger/{jobName}")
    @AuthCheck(mustRole = "admin")
    public Map<String, Object> triggerNow(@PathVariable String jobName,
                                          @RequestParam(defaultValue = "DEFAULT_GROUP") String groupName) {
        return executeAndWrap(() -> {
            quartzJobUtils.triggerNow(jobName, groupName);
            return "任务已手动触发，请查看控制台日志";
        });
    }

    /**
     * 6. 删除任务
     * URL: DELETE /api/quartz/delete/{jobName}?groupName=DEFAULT_GROUP
     */
    @DeleteMapping("/delete/{jobName}")
    @AuthCheck(mustRole = "admin")
    public Map<String, Object> deleteJob(@PathVariable String jobName,
                                         @RequestParam(defaultValue = "DEFAULT_GROUP") String groupName) {
        return executeAndWrap(() -> {
            quartzJobUtils.deleteJob(jobName, groupName);
            return "任务已删除";
        });
    }

    /**
     * 7. 查询所有任务列表
     * URL: GET /api/quartz/list
     */
    @GetMapping("/list")
    @AuthCheck(mustRole = "admin")
    public Map<String, Object> listJobs() throws SchedulerException {
        List<Map<String, String>> jobList = new ArrayList<>();

        for (String groupName : scheduler.getJobGroupNames()) {
            for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
                Map<String, String> jobInfo = buildJobInfoMap(jobKey, groupName);
                jobList.add(jobInfo);
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("count", jobList.size());
        result.put("data", jobList);
        return result;
    }

    /**
     * 8. 查询单个任务详情
     * URL: GET /api/quartz/detail/{jobName}?groupName=DEFAULT_GROUP
     */
    @GetMapping("/detail/{jobName}")
    @AuthCheck(mustRole = "admin")
    public Map<String, Object> getJobDetail(@PathVariable String jobName,
                                            @RequestParam(defaultValue = "DEFAULT_GROUP") String groupName) throws SchedulerException {
        JobKey jobKey = new JobKey(jobName, groupName);

        if (!scheduler.checkExists(jobKey)) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "任务不存在");
            return result;
        }

        Map<String, Object> info = new HashMap<>();
        JobDetail jobDetail = scheduler.getJobDetail(jobKey);
        info.put("jobName", jobKey.getName());
        info.put("groupName", jobKey.getGroup());
        info.put("jobClass", jobDetail.getJobClass().getName());
        info.put("durability", jobDetail.isDurable());

        List<Map<String, Object>> triggerInfos = new ArrayList<>();
        for (Trigger trigger : scheduler.getTriggersOfJob(jobKey)) {
            Map<String, Object> tInfo = new HashMap<>();
            tInfo.put("triggerName", trigger.getKey().getName());
            tInfo.put("state", scheduler.getTriggerState(trigger.getKey()).name());
            tInfo.put("nextFireTime", format_date(trigger.getNextFireTime()));
            tInfo.put("previousFireTime", format_date(trigger.getPreviousFireTime()));

            if (trigger instanceof CronTrigger) {
                tInfo.put("type", "CRON");
                tInfo.put("cronExpression", ((CronTrigger) trigger).getCronExpression());
            } else if (trigger instanceof SimpleTrigger) {
                tInfo.put("type", "SIMPLE");
                tInfo.put("intervalSeconds", ((SimpleTrigger) trigger).getRepeatInterval() / 1000);
            }
            triggerInfos.add(tInfo);
        }
        info.put("triggers", triggerInfos);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", info);
        return result;
    }

    // --- 辅助方法 ---

    private Map<String, String> buildJobInfoMap(JobKey jobKey, String groupName) throws SchedulerException {
        Map<String, String> jobInfo = new HashMap<>();
        jobInfo.put("jobName", jobKey.getName());
        jobInfo.put("groupName", jobKey.getGroup());

        JobDetail jobDetail = scheduler.getJobDetail(jobKey);
        jobInfo.put("jobClass", jobDetail.getJobClass().getName());

        List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);
        if (!triggers.isEmpty()) {
            Trigger trigger = triggers.get(0);
            jobInfo.put("triggerState", scheduler.getTriggerState(trigger.getKey()).name());
            jobInfo.put("nextFireTime", format_date(trigger.getNextFireTime()));

            if (trigger instanceof CronTrigger) {
                jobInfo.put("type", "CRON");
                jobInfo.put("expression", ((CronTrigger) trigger).getCronExpression());
            } else if (trigger instanceof SimpleTrigger) {
                jobInfo.put("type", "SIMPLE");
                jobInfo.put("intervalSeconds", String.valueOf(((SimpleTrigger) trigger).getRepeatInterval() / 1000));
            }
        }
        return jobInfo;
    }

    private String format_date(Date date) {
        if (date == null) return "无";

        // 1. 创建格式化对象
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // 2. 【关键】强制指定时区为中国上海，避免受服务器默认时区影响
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));

        // 3. 返回纯字符串，不带时区缩写
        return sdf.format(date);
    }

    @FunctionalInterface
    interface Action {
        String run() throws Exception;
    }

    private Map<String, Object> executeAndWrap(Action action) {
        Map<String, Object> result = new HashMap<>();
        try {
            String msg = action.run();
            result.put("success", true);
            result.put("message", msg);
        } catch (Exception e) {
            log.error("操作失败", e);
            result.put("success", false);
            result.put("message", "操作失败：" + e.getMessage());
        }
        return result;
    }
    /**
     * 9. 清除所有定时任务 (慎用！)
     * URL: POST /api/quartz/clear-all
     * 注意：这将删除数据库中所有已注册的 Quartz 任务，包括系统自带的和动态创建的。
     *      重启应用后，通过代码自动注册的任务（如 CommandLineRunner）会重新加载，
     *      但纯动态注册且未持久化配置的任务将丢失。
     */
    @PostMapping("/clear-all")
    @AuthCheck(mustRole = "admin")
    public Map<String, Object> clearAllJobs() {
        Map<String, Object> result = new HashMap<>();
        int totalDeleted = 0;

        try {
            log.warn("⚠️ 接收到清除所有任务的请求，开始执行...");

            // 获取所有组名
            List<String> groupNames = scheduler.getJobGroupNames();

            for (String groupName : groupNames) {
                // 获取该组下的所有 JobKey
                Set<JobKey> jobKeys = scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName));

                for (JobKey jobKey : jobKeys) {
                    // 删除 Job (会自动关联删除对应的 Trigger)
                    boolean deleted = scheduler.deleteJob(jobKey);
                    if (deleted) {
                        totalDeleted++;
                        log.info("🗑️ 已删除任务：{}.{}", jobKey.getGroup(), jobKey.getName());
                    }
                }
            }

            result.put("success", true);
            result.put("message", "成功清除所有任务");
            result.put("deletedCount", totalDeleted);
            result.put("warning", "注意：通过代码启动时自动注册的任务会在下次重启后恢复。");

            log.info("✅ 清除任务完毕，共删除 {} 个任务。", totalDeleted);

        } catch (Exception e) {
            log.error("❌ 清除所有任务时发生异常", e);
            result.put("success", false);
            result.put("message", "清除失败：" + e.getMessage());
        }

        return result;
    }

}
