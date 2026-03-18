package com.ninja.wangjia_backend.controller;

import com.ninja.wangjia_backend.common.BaseResponse;
import com.ninja.wangjia_backend.common.ResultUtils;
import com.ninja.wangjia_backend.service.MoneyInfoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


@RestController
@RequestMapping("/")
public class MainController {
    @Resource
    private MoneyInfoService moneyInfoService;

    public MainController(MoneyInfoService moneyInfoService) {
        this.moneyInfoService = moneyInfoService;
    }

    @GetMapping("/health")
    public BaseResponse<String> health() {
        return ResultUtils.success("牛逼");
    }
    @GetMapping("/")
    public BaseResponse<String> index() {
        moneyInfoService.deductFeeBySystem(2033735970922938369L,100.0,"测试");
        return ResultUtils.success("牛逼");
    }
}
