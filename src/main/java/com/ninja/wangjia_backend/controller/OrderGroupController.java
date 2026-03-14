package com.ninja.wangjia_backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ninja.wangjia_backend.annotation.AuthCheck;
import com.ninja.wangjia_backend.common.BaseResponse;
import com.ninja.wangjia_backend.common.ResultUtils;
import com.ninja.wangjia_backend.exception.ErrorCode;
import com.ninja.wangjia_backend.exception.ThrowUtils;
import com.ninja.wangjia_backend.model.entity.Order;
import com.ninja.wangjia_backend.model.entity.OrderGroup;
import com.ninja.wangjia_backend.model.vo.OrderGroupSelectInfoVO;
import com.ninja.wangjia_backend.service.OrderGroupService;
import com.ninja.wangjia_backend.service.OrderService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/orderGroup")
public class OrderGroupController {
    @Resource
    private OrderGroupService orderGroupService;
    @Resource
    private OrderService orderService;
    /**
     * 获取订单组信息:获取state是0的
     * */
    @PostMapping("/list/orderGroupSelectInfo/vo")
    @AuthCheck(mustRole = "user")
    public BaseResponse<List<OrderGroupSelectInfoVO>> listOrderGroupSelectInfo(Long orderId){
        ThrowUtils.throwIf(orderId == null, ErrorCode.PARAMS_ERROR,"订单id为空");
        //根据orderId获取orderGroupId
        Long orderGroupId = orderService.getById(orderId).getOrderGroupId();
        ThrowUtils.throwIf(orderGroupId == null, ErrorCode.PARAMS_ERROR,"订单组id为空");
        List<OrderGroup> orderGroupList = orderGroupService.list(new QueryWrapper<>(new OrderGroup()).eq("state", 0).ne("id",orderGroupId));
        List<OrderGroupSelectInfoVO> orderGroupSelectInfoVOList = new ArrayList<>();
        for(OrderGroup orderGroup : orderGroupList){
            //这里根据每一个订单组去拼接出orderGroupSelectInfo
            OrderGroupSelectInfoVO orderGroupSelectInfoVO = new OrderGroupSelectInfoVO();
            orderGroupSelectInfoVO.setId(orderGroup.getId());
            //这里是订单组中的订单信息
            List<Order> orderList = orderService.list(new QueryWrapper<>(new Order()).eq("orderGroupId", orderGroup.getId()));
            orderGroupSelectInfoVO.setOrderList(orderList);
            orderGroupSelectInfoVOList.add(orderGroupSelectInfoVO);
        }
        return ResultUtils.success(orderGroupSelectInfoVOList);
    }


    /**
     * 获取订单组信息
     * */
    @PostMapping("/list")
    @AuthCheck(mustRole = "user")
    public BaseResponse<List<OrderGroup>> listOrderGroup(){
        return ResultUtils.success(orderGroupService.list());
    }

}
