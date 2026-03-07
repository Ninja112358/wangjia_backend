package com.ninja.wangjia_backend.controller;

import cn.hutool.core.util.ObjUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ninja.wangjia_backend.annotation.AuthCheck;
import com.ninja.wangjia_backend.common.BaseResponse;
import com.ninja.wangjia_backend.common.ResultUtils;
import com.ninja.wangjia_backend.exception.ErrorCode;
import com.ninja.wangjia_backend.exception.ThrowUtils;
import com.ninja.wangjia_backend.model.dto.order.OrderChangeRoomPriceRequest;
import com.ninja.wangjia_backend.model.dto.order.OrderChangeRoomRequest;
import com.ninja.wangjia_backend.model.dto.order.OrderCheckInRequest;
import com.ninja.wangjia_backend.model.entity.MoneyInfo;
import com.ninja.wangjia_backend.model.entity.Order;
import com.ninja.wangjia_backend.model.entity.Room;
import com.ninja.wangjia_backend.service.MoneyInfoService;
import com.ninja.wangjia_backend.service.OrderService;
import com.ninja.wangjia_backend.service.RoomService;
import com.ninja.wangjia_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {
    @Resource
    private OrderService orderService;
    @Resource
    private MoneyInfoService moneyInfoService;
    @Resource
    private RoomService roomService;
    @Autowired
    private UserService userService;

    //订单入住
    @PostMapping("/checkin")
    @AuthCheck(mustRole = "user")
    public BaseResponse<Boolean> checkIn(@RequestBody OrderCheckInRequest orderCheckInRequest) {
        return ResultUtils.success(orderService.checkIn(orderCheckInRequest));
    }
    //订单退房
    @PostMapping("/checkout")
    @AuthCheck(mustRole = "user")
    public BaseResponse<Boolean> checkout(Long orderId) {
        return ResultUtils.success(orderService.checkout(orderId));
    }
    //根据订单id查询订单组的所有订单
    @PostMapping("/list/orderGroupData")
    @AuthCheck(mustRole = "user")
    public BaseResponse<List<Order>> listOrderGroupData(Long orderId) {
        ThrowUtils.throwIf(orderId == null, ErrorCode.PARAMS_ERROR,"订单id为空");
        //判断订单id是否存在
        Order order = orderService.getOne(new QueryWrapper<>(new Order()).eq("id", orderId));
        ThrowUtils.throwIf(order == null, ErrorCode.PARAMS_ERROR, "订单不存在");
        //根据订单中的订单组id查询所有订单
        return ResultUtils.success(orderService.list(new QueryWrapper<>(new Order()).eq("orderGroupId", order.getOrderGroupId())));
    }
    //改房价
    @PostMapping("/room_price/change")
    @AuthCheck(mustRole = "user")
    public BaseResponse<Boolean> changeRoomPrice(@RequestBody OrderChangeRoomPriceRequest orderChangeRoomPriceRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(ObjUtil.hasNull(orderChangeRoomPriceRequest,orderChangeRoomPriceRequest.getOrderId(), orderChangeRoomPriceRequest.getRoomPrice()), ErrorCode.PARAMS_ERROR,"参数为空");
        //判断订单id是否存在
        Order order = orderService.getOne(new QueryWrapper<>(new Order()).eq("id", orderChangeRoomPriceRequest.getOrderId()));
        ThrowUtils.throwIf(order == null, ErrorCode.PARAMS_ERROR, "订单不存在");

        //更改房价之前先更新一下moneyInfo的信息
        MoneyInfo moneyInfo = new MoneyInfo();
        moneyInfo.setOrderId(order.getId());
        moneyInfo.setRoomId(order.getRoomId());
        moneyInfo.setMoney(0.0);
        moneyInfo.setMoneyType("修改房价为" + orderChangeRoomPriceRequest.getRoomPrice() + "元");
        moneyInfo.setPayInfo(orderChangeRoomPriceRequest.getPayInfo());
        moneyInfo.setOperator(userService.getLoginUser(request).getUserAccount());
        moneyInfoService.save(moneyInfo);
        order.setRoomPrice(orderChangeRoomPriceRequest.getRoomPrice());
        return ResultUtils.success(orderService.updateById(order));
    }
    //换房
    @PostMapping("/room/change")
    @AuthCheck(mustRole = "user")
    public BaseResponse<Boolean> changeRoom(@RequestBody OrderChangeRoomRequest orderChangeRoomRequest,HttpServletRequest request) {
        ThrowUtils.throwIf(ObjUtil.hasNull(orderChangeRoomRequest,orderChangeRoomRequest.getOrderId(), orderChangeRoomRequest.getRoomId(), orderChangeRoomRequest.getRoomPrice()), ErrorCode.PARAMS_ERROR,"参数为空");
        //判断订单id是否存在
        Order order = orderService.getOne(new QueryWrapper<>(new Order()).eq("id", orderChangeRoomRequest.getOrderId()));
        ThrowUtils.throwIf(order == null, ErrorCode.PARAMS_ERROR, "订单不存在");
        ThrowUtils.throwIf(order.getOrderState() == 1, ErrorCode.OPERATION_ERROR, "该订单已经退房,不能换房");
        //如果roomId当前有人入住
        Room room = roomService.getOne(new QueryWrapper<>(new Room()).eq("roomId", orderChangeRoomRequest.getRoomId()));
        ThrowUtils.throwIf(room == null, ErrorCode.OPERATION_ERROR, "房间不存在");
        ThrowUtils.throwIf(room.getRoomState() != 0, ErrorCode.OPERATION_ERROR, "该房间状态不支持换房");

        //更新新旧房间信息
        //新房间信息
        Room oldRoom = roomService.getOne(new QueryWrapper<>(new Room()).eq("roomId", order.getRoomId()));
        room.setRoomState(1);
        room.setOrderId(order.getId());
        room.setIsTeam(oldRoom.getIsTeam());
        room.setIsContact(oldRoom.getIsContact());
        room.setRoomCustom(order.getName());
        ThrowUtils.throwIf(!roomService.updateById(room), ErrorCode.OPERATION_ERROR, order.getRoomId() + "房间状态更新失败");
        //旧房间信息
        oldRoom.setRoomState(3);    //将房间状态改为空脏(3)
        oldRoom.setOrderId(null);
        oldRoom.setIsTeam(false);
        oldRoom.setIsContact(false);
        oldRoom.setRoomCustom(null);
        ThrowUtils.throwIf(!roomService.updateById(oldRoom), ErrorCode.OPERATION_ERROR, order.getRoomId() + "房间状态更新失败");


        //更改房间之前先更新一下moneyInfo的信息
        MoneyInfo moneyInfo = new MoneyInfo();
        moneyInfo.setOrderId(order.getId());
        moneyInfo.setRoomId(order.getRoomId());
        moneyInfo.setMoney(0.0);
        moneyInfo.setMoneyType("换房到" + orderChangeRoomRequest.getRoomId());
        moneyInfo.setPayInfo(orderChangeRoomRequest.getPayInfo());
        moneyInfo.setOperator(userService.getLoginUser(request).getUserAccount());
        ThrowUtils.throwIf(!moneyInfoService.save(moneyInfo), ErrorCode.OPERATION_ERROR, "订单保存失败");


        //然后保存订单信息
        order.setRoomId(orderChangeRoomRequest.getRoomId());
        order.setRoomPrice(orderChangeRoomRequest.getRoomPrice());
        order.setRoomType(room.getRoomType());
        ThrowUtils.throwIf(!orderService.updateById(order), ErrorCode.OPERATION_ERROR, "订单保存失败");
        return ResultUtils.success(true);
    }


}
