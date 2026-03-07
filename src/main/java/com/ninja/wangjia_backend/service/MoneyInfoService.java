package com.ninja.wangjia_backend.service;

import com.ninja.wangjia_backend.model.dto.money_info.MoneyInfoFeeRequest;
import com.ninja.wangjia_backend.model.entity.MoneyInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
* @author Administrator
* @description 针对表【money_info(金额信息)】的数据库操作Service
* @createDate 2026-03-06 14:10:08
*/
public interface MoneyInfoService extends IService<MoneyInfo> {
    //根据orderId获取金额信息
    List<MoneyInfo> getMoneyInfoListByOrderId(Long orderId);
    //根据orderId获取整个orderGroup的金额信息
    List<MoneyInfo> listGroupMoneyInfoByOrderId(Long orderId);

    //业务逻辑
    //收款
    boolean pay(MoneyInfoFeeRequest moneyInfoFeeRequest, HttpServletRequest request);
    //扣房费
    boolean deductRoomFee(MoneyInfoFeeRequest moneyInfoFeeRequest,HttpServletRequest request);

}
