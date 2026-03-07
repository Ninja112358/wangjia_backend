package com.ninja.wangjia_backend.service.impl;

import cn.hutool.core.util.ObjUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ninja.wangjia_backend.exception.ErrorCode;
import com.ninja.wangjia_backend.exception.ThrowUtils;
import com.ninja.wangjia_backend.model.dto.order.OrderCheckInRequest;
import com.ninja.wangjia_backend.model.dto.room.RoomCheckInRequest;
import com.ninja.wangjia_backend.model.entity.Order;
import com.ninja.wangjia_backend.model.entity.OrderGroup;
import com.ninja.wangjia_backend.model.entity.Room;
import com.ninja.wangjia_backend.service.OrderGroupService;
import com.ninja.wangjia_backend.service.OrderService;
import com.ninja.wangjia_backend.mapper.OrderMapper;
import com.ninja.wangjia_backend.service.RoomService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

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
    @Resource
    private OrderGroupService orderGroupService;
    @Override
    public Boolean checkIn(OrderCheckInRequest orderCheckInRequest) {
        //检查房间状态
        checkRoomCheckInState(orderCheckInRequest);
        //检查房间完毕开始创建订单
        //1.先创建一个订单组
        OrderGroup orderGroup = orderGroupService.createOrderGroup();
        for (RoomCheckInRequest roomCheckInRequest : orderCheckInRequest.getRoomList()){
            Order order = new Order();
            BeanUtils.copyProperties(orderCheckInRequest, order);
            BeanUtils.copyProperties(roomCheckInRequest, order);
            //将所有的pay只给第一个房间
            order.setPay(roomCheckInRequest.getRoomId().equals(orderCheckInRequest.getRoomList().get(0).getRoomId())?orderCheckInRequest.getPay():0.0);
            order.setRestMoney(order.getPay());
            order.setOrderState(0);
            order.setOrderGroupId(orderGroup.getId());
            //保存订单
            ThrowUtils.throwIf(!this.save(order), ErrorCode.OPERATION_ERROR, order.getRoomId() + "入住失败");
            //更新房间状态
            Room room = roomService.getOne(new QueryWrapper<>(new Room()).eq("roomId", roomCheckInRequest.getRoomId()));
            //更新房间状态
            room.setRoomState(1);
            room.setOrderId(order.getId());
            room.setRoomCustom(order.getName());
            //检查房间是否是团队
            if(order.getCustomType() == 1)
                room.setIsTeam(true);
            //检查房间是否是多个房间,如果是那就联房
            else if(orderCheckInRequest.getRoomList().size() > 1)
                room.setIsContact(true);
            ThrowUtils.throwIf(!roomService.updateById(room), ErrorCode.OPERATION_ERROR, order.getRoomId() + "房间状态更新失败");
        }
        return true;
    }

    @Override
    public void checkRoomCheckInState(OrderCheckInRequest orderCheckInRequest) {
        ThrowUtils.throwIf(ObjUtil.hasNull(orderCheckInRequest,orderCheckInRequest.getRoomList(), orderCheckInRequest.getName(),orderCheckInRequest.getPhone(),orderCheckInRequest.getIdCard(),orderCheckInRequest.getCardType()), ErrorCode.PARAMS_ERROR, "输入不能为空");
        ThrowUtils.throwIf(orderCheckInRequest.getRoomList().isEmpty(), ErrorCode.PARAMS_ERROR, "房间不能为空");
        //检查房间状态
        for (RoomCheckInRequest roomCheckInRequest : orderCheckInRequest.getRoomList()){
            //房间不存在
            Room room = roomService.getOne(new QueryWrapper<>(new Room()).eq("roomId", roomCheckInRequest.getRoomId()));
            ThrowUtils.throwIf(room == null, ErrorCode.PARAMS_ERROR, "房间不存在");
            //如果房间存在，但是房间已经有人住了
            ThrowUtils.throwIf(room.getRoomState() == 1 || room.getRoomState() == 4, ErrorCode.OPERATION_ERROR, "房间" + room.getRoomId() + "已入住");
            //如果房间存在，但是房间已经维修了
            ThrowUtils.throwIf(room.getRoomState() == 2, ErrorCode.OPERATION_ERROR, "房间" + room.getRoomId() + "正在维修");
            //如果房间存在，但是房间已经被锁房了
            ThrowUtils.throwIf(room.getRoomState() == 5, ErrorCode.OPERATION_ERROR, "房间" + room.getRoomId() + "已锁房");
        }
    }

    @Override
    public Boolean checkout(Long orderId) {
        //退房逻辑
        //获取订单信息,判断订单是否存在
        Order order = this.getOne(new QueryWrapper<>(new Order()).eq("id", orderId));
        ThrowUtils.throwIf(order == null, ErrorCode.PARAMS_ERROR, "订单不存在");
        //判断订单状态是否已结
        ThrowUtils.throwIf(order.getOrderState() == 1, ErrorCode.OPERATION_ERROR, "该订单已经退房");
        //将订单状态改为已结,并且更新退房时间
        order.setOrderState(1);
        order.setEndTime(new Date());
        ThrowUtils.throwIf(!this.updateById(order), ErrorCode.OPERATION_ERROR, "退房失败");
        //判断该订单所在订单组的所有订单是不是全是已结状态,如果全是已结状态,那么该订单的订单组必须是已结状态
        List<Order> orderList = this.list(new QueryWrapper<>(new Order()).eq("orderGroupId", order.getOrderGroupId()));
        boolean flag = false;
        for (Order item : orderList) {
            if (item.getOrderState() == 0) {
                flag = true;
                break;
            }
        }
        if(!flag){
            //订单组内的所有订单是已结状态
            OrderGroup orderGroup = orderGroupService.getById(order.getOrderGroupId());
            orderGroup.setState(1);
            ThrowUtils.throwIf(!orderGroupService.updateById(orderGroup), ErrorCode.OPERATION_ERROR, "订单组" + orderGroup.getId() + "状态更新失败");
        }
        //更改房间状态
        //将房间状态改为空脏(3)
        //将房间的isContact和isTeam改为false
        //将房间的orderId改为null
        //将房间的roomCustom改为null
        Room room = roomService.getOne(new QueryWrapper<>(new Room()).eq("roomId", order.getRoomId()));
        room.setRoomState(3);
        room.setIsContact(false);
        room.setIsTeam(false);
        room.setOrderId(null);
        room.setRoomCustom(null);
        ThrowUtils.throwIf(!roomService.updateById(room), ErrorCode.OPERATION_ERROR, "房间" + room.getRoomId() + "状态更新失败");
        return true;
    }


}




