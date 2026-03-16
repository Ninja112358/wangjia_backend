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
import com.ninja.wangjia_backend.model.dto.shop.ShopEnterOrderRequest;
import com.ninja.wangjia_backend.model.entity.MoneyInfo;
import com.ninja.wangjia_backend.model.entity.Order;
import com.ninja.wangjia_backend.model.entity.Shop;
import com.ninja.wangjia_backend.service.MoneyInfoService;
import com.ninja.wangjia_backend.service.OrderService;
import com.ninja.wangjia_backend.service.ShopService;
import com.ninja.wangjia_backend.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("money_info")
public class MoneyInfoController {
    @Resource
    private MoneyInfoService moneyInfoService;
    @Autowired
    private UserService userService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private ShopService shopService;

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

    /*
      小吧的操作
      */
    //shopEnterOrder
    @PostMapping("/deduct/shop")
    @AuthCheck(mustRole = "user")
    public BaseResponse<Boolean> shopEnterOrder(@RequestBody ShopEnterOrderRequest shopEnterOrderRequest, HttpServletRequest request) {
        Order order = orderService.getById(shopEnterOrderRequest.getOrderId());
        ThrowUtils.throwIf(order == null, ErrorCode.NOT_FOUND_ERROR,"订单不存在");
        double money = 0.0; //这里计算出总金额
        StringBuilder str = new StringBuilder();
        //雪碧*3,可乐*4
        List<Shop> shopList = shopEnterOrderRequest.getShopList();
        for (int i = 0; i < shopList.size(); i++) {
            Shop shop = shopList.get(i);
            //最后一个没有逗号
            if (i == shopList.size() - 1)
                str.append(shop.getName()).append("*").append(shop.getNum());
            else
                str.append(shop.getName()).append("*").append(shop.getNum()).append(",");
            money += shop.getPrice() * shop.getNum();

            //需要扣除库存中的剩余
            Shop oldShop = shopService.getById(shop.getId());
            ThrowUtils.throwIf(oldShop == null, ErrorCode.NOT_FOUND_ERROR,"商品:" + shop.getName() + "不存在");
            ThrowUtils.throwIf(oldShop.getNum() < shop.getNum(), ErrorCode.OPERATION_ERROR,"商品库存不足");
            oldShop.setNum(oldShop.getNum() - shop.getNum());
            shopService.saveOrUpdate(oldShop);
        }
        //然后扣费
        order.setRestMoney(order.getRestMoney() - money);
        order.setShopConsume(order.getShopConsume() + money);
        orderService.saveOrUpdate(order);

        //录入金额信息
        MoneyInfo moneyInfo = new MoneyInfo();
        moneyInfo.setOrderId(order.getId());
        moneyInfo.setMoneyType("小吧入账");
        moneyInfo.setMoney(money);
        moneyInfo.setOperator(userService.getLoginUser(request).getUserAccount());
        moneyInfo.setRoomId(order.getRoomId());
        moneyInfo.setPayInfo(str.toString());
        moneyInfo.setPayTime(new Date());
        System.out.println(moneyInfo);
        moneyInfoService.save(moneyInfo);
        return ResultUtils.success(true);
    }

    /*
      对金额信息的删改查(没有增)
      @author Ninja
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
