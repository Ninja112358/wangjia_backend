package com.ninja.wangjia_backend.service.impl;

import cn.hutool.core.util.ObjUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ninja.wangjia_backend.constant.JobConstant;
import com.ninja.wangjia_backend.exception.BusinessException;
import com.ninja.wangjia_backend.exception.ErrorCode;
import com.ninja.wangjia_backend.exception.ThrowUtils;
import com.ninja.wangjia_backend.model.dto.money_info.MoneyInfoFeeRequest;
import com.ninja.wangjia_backend.model.dto.order.OrderCheckInRequest;
import com.ninja.wangjia_backend.model.dto.order.OrderQueryRequest;
import com.ninja.wangjia_backend.model.dto.room.RoomCheckInRequest;
import com.ninja.wangjia_backend.model.entity.Order;
import com.ninja.wangjia_backend.model.entity.OrderGroup;
import com.ninja.wangjia_backend.model.entity.Room;
import com.ninja.wangjia_backend.quartz.jobs.HourDeductJob;
import com.ninja.wangjia_backend.quartz.jobs.TestJob;
import com.ninja.wangjia_backend.service.*;
import com.ninja.wangjia_backend.mapper.OrderMapper;
import com.ninja.wangjia_backend.utils.QuartzJobUtils;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDataMap;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

/**
* @author Administrator
* @description 针对表【order(订单信息)】的数据库操作Service实现
* @createDate 2026-03-03 23:40:28
*/
@Service
@Slf4j
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order>
    implements OrderService{
    @Resource
    private RoomService roomService;
    @Resource
    private OrderGroupService orderGroupService;
    @Resource
    private QuartzJobUtils quartzJobUtils;
    @Autowired
    private UserService userService;

    @Override
    public Long checkIn(OrderCheckInRequest orderCheckInRequest) {
        //检查房间状态
        checkRoomCheckInState(orderCheckInRequest);
        //检查房间完毕开始创建订单
        //1.先创建一个订单组
        OrderGroup orderGroup = orderGroupService.createOrderGroup();
        Long orderId = null;
        for (RoomCheckInRequest roomCheckInRequest : orderCheckInRequest.getRoomList()){
            Order order = new Order();
            BeanUtils.copyProperties(orderCheckInRequest, order);
            BeanUtils.copyProperties(roomCheckInRequest, order);
            //将所有的pay只给第一个房间
            order.setPay(0.0);
            order.setRestMoney(0.0);
            order.setOrderState(0);
            order.setOrderGroupId(orderGroup.getId());
            //保存订单
            ThrowUtils.throwIf(!this.save(order), ErrorCode.OPERATION_ERROR, order.getRoomId() + "入住失败");
            //更新房间状态
            Room room = roomService.getOne(new QueryWrapper<>(new Room()).eq("roomId", roomCheckInRequest.getRoomId()));
            //获取第一个房间的orderId
            if(orderId == null)
                orderId = order.getId();
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



            //前面都成功后这里要对订单开启定时器
            String jobName = JobConstant.ORDER_JOB_NAME_PREFIX + order.getRoomId() + "_" + order.getId();
            String groupName = JobConstant.ORDER_JOB_GROUP;
            JobDataMap params = new JobDataMap();
            params.put("orderId", order.getId());
            // 3600 秒 = 1 小时
            quartzJobUtils.createOneTimeDelayJob(
                    HourDeductJob.class,
                    jobName,
                    groupName,
                    3600,
                    params
            );




            ThrowUtils.throwIf(!roomService.updateById(room), ErrorCode.OPERATION_ERROR, order.getRoomId() + "房间状态更新失败");
        }

        return orderId;
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


    @Override
    public Wrapper<Order> getQueryWrapper(OrderQueryRequest orderQueryRequest) {
        if(orderQueryRequest == null)
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        Long id = orderQueryRequest.getId();
        String name = orderQueryRequest.getName();
        String phone = orderQueryRequest.getPhone();
        String idCard = orderQueryRequest.getIdCard();
        String roomId = orderQueryRequest.getRoomId();
        String roomType = orderQueryRequest.getRoomType();
        Long orderGroupId = orderQueryRequest.getOrderGroupId();
        Integer customType = orderQueryRequest.getCustomType();
        Integer orderState = orderQueryRequest.getOrderState();
        String sortField = orderQueryRequest.getSortField();
        String sortOrder = orderQueryRequest.getSortOrder();

        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(ObjUtil.isNotNull(id), "id", id);
        queryWrapper.like(ObjUtil.isNotNull(name), "name", name);
        queryWrapper.like(ObjUtil.isNotNull(phone), "phone", phone);
        queryWrapper.like(ObjUtil.isNotNull(idCard), "idCard", idCard);
        queryWrapper.like(ObjUtil.isNotNull(roomId), "roomId", roomId);
        queryWrapper.like(ObjUtil.isNotNull(roomType), "roomType", roomType);
        queryWrapper.eq(ObjUtil.isNotNull(orderGroupId), "orderGroupId", orderGroupId);
        if(customType != 2)
            queryWrapper.like(ObjUtil.isNotNull(customType), "customType", customType);
        if(orderState != 2)
            queryWrapper.like(ObjUtil.isNotNull(orderState), "orderState", orderState);
        queryWrapper.orderBy(ObjUtil.isNotNull(sortField), sortOrder.equals("ascend"), sortField);
        return queryWrapper;
    }

    @Override
    public Boolean checkoutCancel(Long orderId, HttpServletRequest request) {
        Order order = this.getOne(new QueryWrapper<>(new Order()).eq("id", orderId));
        ThrowUtils.throwIf(order == null, ErrorCode.PARAMS_ERROR, "订单不存在");
        ThrowUtils.throwIf(order.getOrderState() != 1, ErrorCode.OPERATION_ERROR, "该订单未退房");
        //判断撤销退房的时间是否是退房时间的十分钟内
        long one_minute = 60 * 1000;
        long one_hour = 60 * one_minute;
        if(!userService.getLoginUser(request).getUserRole().equals("admin"))
            ThrowUtils.throwIf(new Date().getTime() - order.getEndTime().getTime() > 24 * one_hour, ErrorCode.OPERATION_ERROR, "撤销退房时间超过1天");

        //取消退房:
        // 判断房间是否有是在住状态,如果不是则更新房间状态为在住并且填充用户信息
        // 设置订单状态为未结
        // 设置订单组状态为0
        Room room = roomService.getOne(new QueryWrapper<>(new Room()).eq("roomId", order.getRoomId()));
        ThrowUtils.throwIf(room.getRoomState() == 1 || room.getRoomState() == 4, ErrorCode.OPERATION_ERROR, "房间" + room.getRoomId() + "状态不支持取消退房");

        order.setOrderState(0);
        ThrowUtils.throwIf(!this.updateById(order), ErrorCode.OPERATION_ERROR, "订单" + order.getId() + "状态更新失败");
        OrderGroup orderGroup = orderGroupService.getById(order.getOrderGroupId());
        orderGroup.setState(0);
        ThrowUtils.throwIf(!orderGroupService.updateById(orderGroup), ErrorCode.OPERATION_ERROR, "订单组" + orderGroup.getId() + "状态更新失败");

        room.setRoomState(1);
        room.setOrderId(orderId);
        room.setRoomCustom(order.getName());
        long count = this.count(new QueryWrapper<>(new Order()).eq("orderGroupId", order.getOrderGroupId()));
        room.setIsContact(count > 1);
        room.setIsTeam(order.getCustomType() == 1);
        ThrowUtils.throwIf(!roomService.updateById(room), ErrorCode.OPERATION_ERROR, "房间" + room.getRoomId() + "状态更新失败");
        return true;
    }


}




