package com.ninja.wangjia_backend.quartz.jobs;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
import java.util.Date;
import java.util.List;

@DisallowConcurrentExecution    // 禁止并发执行
@Component
public class DalyDeductJob extends QuartzJobBean {

    @Resource
    private MoneyInfoService moneyInfoService;
    @Resource
    private OrderService orderService;
    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        //这里写每日14:30的扣费逻辑就可以了
        //扣费只扣状态是未结状态的订单组里的未结的订单
        System.out.println("每日定时扣费任务执行时间：" + new Date());
        //这里获取所有未结状态的order
        List<Order> orderList = orderService.list(new QueryWrapper<>(new Order()).eq("orderState", 0));
        for (Order order : orderList) {
            //判断该订单是否可以扣费
            if(order.getDeductState() == 1){
                //这里需要执行扣费
                moneyInfoService.deductFeeBySystem(order.getId(),order.getRoomPrice(),"每日14:30扣房费");
                System.out.println("房间" + order.getRoomId() + "的订单已扣费");
            }
            else if(order.getDeductState() > 1){
                order.setDeductState(order.getDeductState() - 1);
                orderService.updateById(order);
                System.out.println("房间" + order.getRoomId() + "的订单下一次会扣费");
            }
        }
    }
}
