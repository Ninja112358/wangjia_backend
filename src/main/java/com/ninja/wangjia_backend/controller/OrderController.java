package com.ninja.wangjia_backend.controller;

import cn.hutool.core.util.ObjUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ninja.wangjia_backend.annotation.AuthCheck;
import com.ninja.wangjia_backend.common.BaseResponse;
import com.ninja.wangjia_backend.common.ResultUtils;
import com.ninja.wangjia_backend.exception.ErrorCode;
import com.ninja.wangjia_backend.exception.ThrowUtils;
import com.ninja.wangjia_backend.model.dto.money_info.MoneyInfoFeeRequest;
import com.ninja.wangjia_backend.model.dto.order.*;
import com.ninja.wangjia_backend.model.dto.room.RoomQueryRequest;
import com.ninja.wangjia_backend.model.entity.MoneyInfo;
import com.ninja.wangjia_backend.model.entity.Order;
import com.ninja.wangjia_backend.model.entity.OrderGroup;
import com.ninja.wangjia_backend.model.entity.Room;
import com.ninja.wangjia_backend.model.vo.OrderGroupSelectInfoVO;
import com.ninja.wangjia_backend.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/order")
public class OrderController {
    @Resource
    private OrderService orderService;
    @Resource
    private MoneyInfoService moneyInfoService;
    @Resource
    private RoomService roomService;
    @Resource
    private UserService userService;
    @Resource
    private OrderGroupService orderGroupService;

    //订单入住
    @PostMapping("/checkin")
    @AuthCheck(mustRole = "user")
    public BaseResponse<Boolean> checkIn(@RequestBody OrderCheckInRequest orderCheckInRequest,HttpServletRequest request) {
        Long orderId = orderService.checkIn(orderCheckInRequest);
        //如果用户交钱了
        if(orderCheckInRequest.getPay() != 0){
            moneyInfoService.pay(new MoneyInfoFeeRequest(orderId,orderCheckInRequest.getPay(),"入住收款",new Date()),request);
        }
        return ResultUtils.success(true);
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
        //如果该订单已经退房,则不能改房价
        ThrowUtils.throwIf(order.getOrderState() == 1,ErrorCode.OPERATION_ERROR,"该房间已经退房,不能改房价");

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

    @PostMapping("/contact")
    @AuthCheck(mustRole = "user")
    public BaseResponse<Boolean> contactOrder(@RequestBody OrderContactRequest orderContactRequest) {
        ThrowUtils.throwIf(orderContactRequest == null, ErrorCode.PARAMS_ERROR);
        Long orderGroupId = orderContactRequest.getOrderGroupId();
        List<OrderGroupSelectInfoVO> orderGroupSelectInfoList = orderContactRequest.getOrderGroupSelectInfoList();
        ThrowUtils.throwIf(ObjUtil.hasNull(orderGroupId,orderGroupSelectInfoList),ErrorCode.PARAMS_ERROR,"订单组id或联房信息为空");
        //然后查询orderGroup的订单
        Order order = orderService.getOne(new QueryWrapper<>(new Order()).eq("orderGroupId", orderGroupId),false);

        ThrowUtils.throwIf(order == null,ErrorCode.PARAMS_ERROR,"订单组不存在");
        //获取一下顾客类型:如果这些订单组中出现一个团队的，那么合起来就全都是团队类型，不然就全是联房类型
        Integer customType = order.getCustomType();
        boolean isTeam = customType == 1;
        //开始联房
        //将所有信息全部整合成一个List<Long> orderGroupIdList
        List<Long> orderGroupIdList = new ArrayList<>();
        orderGroupIdList.add(orderGroupId);
        for (OrderGroupSelectInfoVO item : orderGroupSelectInfoList){
            if(!item.getOrderList().isEmpty()){
                isTeam = isTeam || (item.getOrderList().get(0).getCustomType() == 1);
                orderGroupIdList.add(item.getId());
            }
        }
        //这里求出了isTeam以及orderGroupIdList
        //这里要更新一下所有的订单的顾客状态,并且还要更新所有房间的联房和团队状态
        for(Long item : orderGroupIdList){
            //这里根据OrderGroupId求出对应的OrderList
            List<Room> roomList = new ArrayList<>();
            List<Order> orderList = orderService.list(new QueryWrapper<>(new Order()).eq("orderGroupId", item));
            for (Order orderItem:orderList){
                orderItem.setOrderGroupId(orderGroupId);    //设置订单组id
                orderItem.setCustomType(isTeam?1:0);        //根据isTeam去设置顾客类型
                Room room = roomService.getOne(new QueryWrapper<>(new Room()).eq("roomId", orderItem.getRoomId()));
                room.setIsContact(true);
                room.setIsTeam(isTeam);
                roomList.add(room);
            }
            roomService.saveOrUpdateBatch(roomList);
            orderService.saveOrUpdateBatch(orderList);
            if(!Objects.equals(item, orderGroupId)){
                //最后将所有除了传入参数的orderGroup，剩下的orderGroup全部删除
                orderGroupService.removeById(item);
            }
        }

        return ResultUtils.success(true);
    }

    @PostMapping("/list/page")
    @AuthCheck(mustRole = "user")
    public BaseResponse<Page<Order>> listOrderByPage(@RequestBody OrderQueryRequest orderQueryRequest) {
        ThrowUtils.throwIf(orderQueryRequest == null, ErrorCode.PARAMS_ERROR);
        long current = orderQueryRequest.getCurrent();
        long pageSize = orderQueryRequest.getPageSize();
        Page<Order> orderPage = orderService.page(new Page<>(current, pageSize),orderService.getQueryWrapper(orderQueryRequest));
        return ResultUtils.success(orderPage);
    }

    @PostMapping("/update")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<Boolean> updateOrder(@RequestBody OrderUpdateRequest orderUpdateRequest) {
        ThrowUtils.throwIf(orderUpdateRequest == null, ErrorCode.PARAMS_ERROR);
        //判断订单id
        Order order = orderService.getById(orderUpdateRequest.getId());
        ThrowUtils.throwIf(order == null,ErrorCode.PARAMS_ERROR,"id不存在");
        BeanUtils.copyProperties(orderUpdateRequest,order);
        return ResultUtils.success(orderService.saveOrUpdate(order));
    }






}
