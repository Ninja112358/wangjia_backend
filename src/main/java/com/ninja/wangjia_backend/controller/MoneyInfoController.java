package com.ninja.wangjia_backend.controller;

import com.ninja.wangjia_backend.annotation.AuthCheck;
import com.ninja.wangjia_backend.common.BaseResponse;
import com.ninja.wangjia_backend.common.ResultUtils;
import com.ninja.wangjia_backend.model.dto.money_info.MoneyInfoFeeRequest;
import com.ninja.wangjia_backend.model.entity.MoneyInfo;
import com.ninja.wangjia_backend.service.MoneyInfoService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("money_info")
public class MoneyInfoController {
    @Resource
    private MoneyInfoService moneyInfoService;
    @PostMapping("/pay")
    @AuthCheck(mustRole = "user")
    public BaseResponse<Boolean> pay(@RequestBody MoneyInfoFeeRequest moneyInfoFeeRequest, HttpServletRequest request) {
        return ResultUtils.success(moneyInfoService.pay(moneyInfoFeeRequest,request));
    }
    @PostMapping("/deduct")
    @AuthCheck(mustRole = "user")
    public BaseResponse<Boolean> deduct(@RequestBody MoneyInfoFeeRequest moneyInfoFeeRequest,HttpServletRequest request) {
        return ResultUtils.success(moneyInfoService.deductRoomFee(moneyInfoFeeRequest,request));
    }
    @PostMapping("/list/orderId")
    @AuthCheck(mustRole = "user")
    public BaseResponse<List<MoneyInfo>> listMoneyInfoByOrderId(Long orderId) {
        return ResultUtils.success(moneyInfoService.getMoneyInfoListByOrderId(orderId));
    }
    @PostMapping("/list/group/orderId")
    @AuthCheck(mustRole = "user")
    public BaseResponse<List<MoneyInfo>> listGroupMoneyInfoByOrderId(Long orderId) {
        return ResultUtils.success(moneyInfoService.listGroupMoneyInfoByOrderId(orderId));
    }
}
