package com.ninja.wangjia_backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ninja.wangjia_backend.common.BaseResponse;
import com.ninja.wangjia_backend.common.ResultUtils;
import com.ninja.wangjia_backend.exception.ErrorCode;
import com.ninja.wangjia_backend.exception.ThrowUtils;
import com.ninja.wangjia_backend.model.dto.order.OrderCheckInRequest;
import com.ninja.wangjia_backend.model.entity.Order;
import com.ninja.wangjia_backend.service.OrderService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {
    @Resource
    private OrderService orderService;
    @PostMapping("/checkin")
    public BaseResponse<Boolean> checkIn(@RequestBody OrderCheckInRequest orderCheckInRequest) {
        return ResultUtils.success(orderService.checkIn(orderCheckInRequest));
    }
}
