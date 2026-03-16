package com.ninja.wangjia_backend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ninja.wangjia_backend.annotation.AuthCheck;
import com.ninja.wangjia_backend.common.BaseResponse;
import com.ninja.wangjia_backend.common.DeleteRequest;
import com.ninja.wangjia_backend.common.ResultUtils;
import com.ninja.wangjia_backend.exception.ErrorCode;
import com.ninja.wangjia_backend.exception.ThrowUtils;
import com.ninja.wangjia_backend.model.dto.money_info.MoneyInfoFeeRequest;
import com.ninja.wangjia_backend.model.dto.money_info.MoneyInfoQueryRequest;
import com.ninja.wangjia_backend.model.dto.money_info.MoneyInfoUpdateRequest;
import com.ninja.wangjia_backend.model.dto.order.OrderQueryRequest;
import com.ninja.wangjia_backend.model.entity.MoneyInfo;
import com.ninja.wangjia_backend.model.entity.Order;
import com.ninja.wangjia_backend.service.MoneyInfoService;
import org.springframework.beans.BeanUtils;
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
        System.out.println(moneyInfoFeeRequest.getOrderId());
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

    /**
     * 对金额信息的删改查(没有增)
     * @author Ninja
     * */
    // 金额信息的分页查询
    @PostMapping("/list/page")
    @AuthCheck(mustRole = "user")
    public BaseResponse<Page<MoneyInfo>> listMoneyInfoByPage(@RequestBody MoneyInfoQueryRequest moneyInfoQueryRequest) {
        ThrowUtils.throwIf(moneyInfoQueryRequest == null, ErrorCode.PARAMS_ERROR);
        long current = moneyInfoQueryRequest.getCurrent();
        long pageSize = moneyInfoQueryRequest.getPageSize();
        Page<MoneyInfo> moneyInfoPage = moneyInfoService.page(new Page<>(current, pageSize),moneyInfoService.getQueryWrapper(moneyInfoQueryRequest));
        return ResultUtils.success(moneyInfoPage);
    }

    // 金额信息的更改
    @PostMapping("/update")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<Boolean> updateMoneyInfo(@RequestBody MoneyInfoUpdateRequest moneyInfoUpdateRequest) {
        ThrowUtils.throwIf(moneyInfoUpdateRequest == null, ErrorCode.PARAMS_ERROR);
        MoneyInfo moneyInfo = new MoneyInfo();
        BeanUtils.copyProperties(moneyInfoUpdateRequest,moneyInfo);
        return ResultUtils.success(moneyInfoService.updateById(moneyInfo));
    }
    // 删除金额信息
    @PostMapping("/delete")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<Boolean> deleteMoneyInfo(@RequestBody DeleteRequest deleteRequest) {
        ThrowUtils.throwIf(deleteRequest == null, ErrorCode.PARAMS_ERROR);
        return ResultUtils.success(moneyInfoService.removeById(deleteRequest.getId()));
    }


}
