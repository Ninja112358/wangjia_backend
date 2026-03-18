package com.ninja.wangjia_backend.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.ninja.wangjia_backend.model.dto.order.OrderCheckInRequest;
import com.ninja.wangjia_backend.model.dto.order.OrderQueryRequest;
import com.ninja.wangjia_backend.model.dto.room.RoomCheckInRequest;
import com.ninja.wangjia_backend.model.entity.Order;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author Administrator
* @description 针对表【order(订单信息)】的数据库操作Service
* @createDate 2026-03-03 23:40:28
*/
public interface OrderService extends IService<Order> {

    Long checkIn(OrderCheckInRequest orderCheckInRequest);
    void checkRoomCheckInState(OrderCheckInRequest orderCheckInRequest);

    Boolean checkout(Long orderId);

    Wrapper<Order> getQueryWrapper(OrderQueryRequest orderQueryRequest);

    Boolean checkoutCancel(Long orderId, HttpServletRequest request);
}
