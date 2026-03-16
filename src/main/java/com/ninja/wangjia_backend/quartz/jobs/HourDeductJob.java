package com.ninja.wangjia_backend.quartz.jobs;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ninja.wangjia_backend.exception.ErrorCode;
import com.ninja.wangjia_backend.exception.ThrowUtils;
import com.ninja.wangjia_backend.model.entity.Order;
import com.ninja.wangjia_backend.service.MoneyInfoService;
import com.ninja.wangjia_backend.service.OrderGroupService;
import com.ninja.wangjia_backend.service.OrderService;
import lombok.Data;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.Date;

@DisallowConcurrentExecution    // 禁止并发执行
@Component
@Data
public class HourDeductJob extends QuartzJobBean {
    private Long orderId;

    @Resource
    private MoneyInfoService moneyInfoService;
    @Resource
    private OrderService orderService;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        System.out.println("订单" + orderId + "的一小时后扣费任务执行时间：" + new Date());
        //获取订单
        Order order = orderService.getById(orderId);
        ThrowUtils.throwIf(order == null, ErrorCode.PARAMS_ERROR, "订单不存在");
        ThrowUtils.throwIf(order.getOrderState() == 1, ErrorCode.OPERATION_ERROR, "订单状态为已结");
        moneyInfoService.deductFeeBySystem(order.getId(),order.getRoomPrice(),"一个小时后扣费");
        //扣费后需要更新deductState
        //在第一次扣费的时候更新这个deduct_state
        // 如果入住时间在五点之前，第一次扣费时候更新这个deduct_state为1表示从今天开始每天都需要扣费
        // 如果入住时间14:30之后，第一次扣费时候更新这个deduct_state为1表示明天开始每天都需要扣费
        // 如果入住时间是五点之后14:30之前，第一次扣费时候更新这个deduct_state为2表示明天开始每天都需要扣费

        if (order.getDeductState() == null || order.getDeductState() == 0) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            int checkInHour = calendar.get(Calendar.HOUR_OF_DAY);
            int checkInMinute = calendar.get(Calendar.MINUTE);

            int deductState;
            if (checkInHour < 5) {
                deductState = 1;
            } else if (checkInHour < 14 || (checkInHour == 14 && checkInMinute < 30)) {
                deductState = 2;
            } else {
                deductState = 1;
            }
            order.setDeductState(deductState);
            orderService.updateById(order);
        }
    }
}
