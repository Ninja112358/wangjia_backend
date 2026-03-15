package com.ninja.wangjia_backend.service.impl;

import cn.hutool.core.util.ObjUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ninja.wangjia_backend.exception.ErrorCode;
import com.ninja.wangjia_backend.exception.ThrowUtils;
import com.ninja.wangjia_backend.model.dto.money_info.MoneyInfoFeeRequest;
import com.ninja.wangjia_backend.model.entity.MoneyInfo;
import com.ninja.wangjia_backend.model.entity.Order;
import com.ninja.wangjia_backend.service.MoneyInfoService;
import com.ninja.wangjia_backend.mapper.MoneyInfoMapper;
import com.ninja.wangjia_backend.service.OrderService;
import com.ninja.wangjia_backend.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
* @author Administrator
* @description 针对表【money_info(金额信息)】的数据库操作Service实现
* @createDate 2026-03-06 14:10:08
*/
@Service
public class MoneyInfoServiceImpl extends ServiceImpl<MoneyInfoMapper, MoneyInfo>
    implements MoneyInfoService{
    @Resource
    private OrderService orderService;
    @Resource
    private UserService userService;
    @Override
    public List<MoneyInfo> getMoneyInfoListByOrderId(Long orderId) {
        ThrowUtils.throwIf(orderId == null, ErrorCode.PARAMS_ERROR,"参数为空");
        return this.list(new QueryWrapper<>(new MoneyInfo()).eq("orderId", orderId));
    }

    @Override
    public List<MoneyInfo> listGroupMoneyInfoByOrderId(Long orderId) {
        //根据orderId获取整个orderGroup的金额信息
        //先根据orderId获取到orderGroupId
        Order order = orderService.getById(orderId);
        ThrowUtils.throwIf(order == null, ErrorCode.NOT_FOUND_ERROR,"订单不存在");
        //根据orderGroupId获取到所有的order
        List<Order> orders = orderService.list(new QueryWrapper<>(new Order()).eq("orderGroupId", order.getOrderGroupId()));
        List<MoneyInfo> moneyInfos = new ArrayList<>();
        //根据所有的orderId获取到所有的金额信息
        for (Order item : orders)
            moneyInfos.addAll(this.list(new QueryWrapper<>(new MoneyInfo()).eq("orderId", item.getId())));
        return moneyInfos;
    }

    @Override
    public boolean pay(MoneyInfoFeeRequest moneyInfoFeeRequest,HttpServletRequest request) {
        ThrowUtils.throwIf(ObjUtil.hasNull(moneyInfoFeeRequest, moneyInfoFeeRequest.getOrderId(), moneyInfoFeeRequest.getMoney(), moneyInfoFeeRequest.getPayInfo()), ErrorCode.PARAMS_ERROR,"参数为空");
        //根据orderId获取order,随后需要用到order中的roomId
        //获取到订单信息后,进行收费并且更新订单信息
        Order order = orderService.getById(moneyInfoFeeRequest.getOrderId());
        ThrowUtils.throwIf(order == null, ErrorCode.NOT_FOUND_ERROR,"订单不存在");

        //判断订单是否已结
        ThrowUtils.throwIf(order.getOrderState() == 1, ErrorCode.OPERATION_ERROR,"该订单已经退房,不能收款");
        order.setPay(order.getPay() + moneyInfoFeeRequest.getMoney());
        order.setRestMoney(order.getRestMoney() + moneyInfoFeeRequest.getMoney());
        ThrowUtils.throwIf(!orderService.updateById(order), ErrorCode.OPERATION_ERROR,"订单更新失败");
        //更新moneyInfo
        MoneyInfo moneyInfo = getMoneyInfo(moneyInfoFeeRequest,order.getRoomId(),request);
        moneyInfo.setMoneyType("收款");
        return this.save(moneyInfo);
    }

    @Override
    public boolean deductRoomFee(MoneyInfoFeeRequest moneyInfoFeeRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(ObjUtil.hasNull(moneyInfoFeeRequest, moneyInfoFeeRequest.getOrderId(), moneyInfoFeeRequest.getMoney(), moneyInfoFeeRequest.getPayInfo()), ErrorCode.PARAMS_ERROR,"参数为空");
        //根据orderId获取order,随后需要用到order中的roomId
        //获取到订单信息后,进行扣费并且更新订单信息
        Order order = orderService.getById(moneyInfoFeeRequest.getOrderId());
        ThrowUtils.throwIf(order == null, ErrorCode.NOT_FOUND_ERROR,"订单不存在");
        //判断订单是否已结
        ThrowUtils.throwIf(order.getOrderState() == 1, ErrorCode.OPERATION_ERROR,"该订单已经退房,不能扣费");

        order.setRestMoney(order.getRestMoney() - moneyInfoFeeRequest.getMoney());
        order.setConsume(order.getConsume() + moneyInfoFeeRequest.getMoney());
        ThrowUtils.throwIf(!orderService.updateById(order), ErrorCode.OPERATION_ERROR,"订单更新失败");
        //更新moneyInfo
        MoneyInfo moneyInfo = getMoneyInfo(moneyInfoFeeRequest,order.getRoomId(),request);
        moneyInfo.setMoneyType("扣费");
        return this.save(moneyInfo);
    }

    @Override
    public boolean deductFeeBySystem(Long orderId, Double money, String payInfo) {
        ThrowUtils.throwIf(ObjUtil.hasNull(orderId, money,payInfo), ErrorCode.PARAMS_ERROR,"参数为空");
        Order order = orderService.getById(orderId);
        ThrowUtils.throwIf(order == null, ErrorCode.NOT_FOUND_ERROR,"订单不存在");
        //判断订单是否已结
        ThrowUtils.throwIf(order.getOrderState() == 1, ErrorCode.OPERATION_ERROR,"该订单已经退房,不能扣费");
        order.setRestMoney(order.getRestMoney() - money);
        order.setConsume(order.getConsume() + money);
        ThrowUtils.throwIf(!orderService.updateById(order), ErrorCode.OPERATION_ERROR,"订单更新失败");
        //更新moneyInfo
        MoneyInfo moneyInfo = new MoneyInfo();
        moneyInfo.setMoney(money);
        moneyInfo.setOrderId(orderId);
        moneyInfo.setPayInfo(payInfo);
        moneyInfo.setRoomId(order.getRoomId());
        moneyInfo.setOperator("System");
        moneyInfo.setMoneyType("扣费");
        return this.save(moneyInfo);
    }

    private MoneyInfo getMoneyInfo(MoneyInfoFeeRequest moneyInfoFeeRequest, String roomId, HttpServletRequest request) {
        MoneyInfo moneyInfo = new MoneyInfo();
        BeanUtils.copyProperties(moneyInfoFeeRequest, moneyInfo);
        moneyInfo.setRoomId(roomId);
        moneyInfo.setOperator(userService.getLoginUser(request).getUserAccount());
        if (moneyInfoFeeRequest.getPayTime() == null)
            moneyInfo.setPayTime(new Date());
        else{
            //这里要判断payTime是否是当前时间的十分钟之内
            ThrowUtils.throwIf(moneyInfoFeeRequest.getPayTime().getTime() < System.currentTimeMillis() - 10 * 60 * 1000, ErrorCode.OPERATION_ERROR,"支付时间不能早于当前时间10分钟");
            //这里要判断payTime是否比当前时间晚,如果比当前时间晚就报错
            ThrowUtils.throwIf(moneyInfoFeeRequest.getPayTime().getTime() > System.currentTimeMillis(), ErrorCode.OPERATION_ERROR,"支付时间不能晚于当前时间");
        }
        return moneyInfo;
    }

}




