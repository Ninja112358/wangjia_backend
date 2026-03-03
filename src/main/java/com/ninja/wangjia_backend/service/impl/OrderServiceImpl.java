package com.ninja.wangjia_backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ninja.wangjia_backend.exception.ErrorCode;
import com.ninja.wangjia_backend.exception.ThrowUtils;
import com.ninja.wangjia_backend.model.dto.order.OrderCheckInRequest;
import com.ninja.wangjia_backend.model.entity.Order;
import com.ninja.wangjia_backend.model.entity.Room;
import com.ninja.wangjia_backend.service.OrderService;
import com.ninja.wangjia_backend.mapper.OrderMapper;
import com.ninja.wangjia_backend.service.RoomService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
* @author Administrator
* @description 针对表【order(订单信息)】的数据库操作Service实现
* @createDate 2026-03-03 23:40:28
*/
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order>
    implements OrderService{
    @Resource
    private RoomService roomService;
    @Override
    public Boolean checkIn(OrderCheckInRequest orderCheckInRequest) {
        ThrowUtils.throwIf(orderCheckInRequest == null, ErrorCode.PARAMS_ERROR, "参数为空");
        ThrowUtils.throwIf(orderCheckInRequest.getRoomId() == null ||
                orderCheckInRequest.getName() == null ||
                orderCheckInRequest.getPhone() == null ||
                orderCheckInRequest.getIdCard() == null ||
                orderCheckInRequest.getCardType() == null, ErrorCode.PARAMS_ERROR, "输入不能为空");
        //检查房间状态
        //房间不存在
        Room room = roomService.getOne(new QueryWrapper<>(new Room()).eq("roomId", orderCheckInRequest.getRoomId()));
        ThrowUtils.throwIf(room == null, ErrorCode.PARAMS_ERROR, "房间不存在");
        //如果房间存在，但是房间已经有人住了
        ThrowUtils.throwIf(room.getRoomState() == 1 || room.getRoomState() == 4, ErrorCode.OPERATION_ERROR, "房间已入住");


        //检查房间完毕开始整订单
        Order order = new Order();
        BeanUtils.copyProperties(orderCheckInRequest, order);
        order.setOrderGroupId(20L);
        order.setOrderState(0);
        System.out.println(order);
        //保存订单
        ThrowUtils.throwIf(!this.save(order), ErrorCode.OPERATION_ERROR, "入住失败");


        //更新房间状态
        room.setRoomState(1);
        room.setOrderId(order.getId());
        room.setRoomCustom(order.getName());
        if(order.getCustomType() == 1)
            room.setIsTeam(true);
        ThrowUtils.throwIf(!roomService.updateById(room), ErrorCode.OPERATION_ERROR, "入住失败");
        return true;
    }
}




