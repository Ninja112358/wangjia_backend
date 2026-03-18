package com.ninja.wangjia_backend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ninja.wangjia_backend.annotation.AuthCheck;
import com.ninja.wangjia_backend.common.BaseResponse;
import com.ninja.wangjia_backend.common.DeleteRequest;
import com.ninja.wangjia_backend.common.ResultUtils;
import com.ninja.wangjia_backend.exception.ErrorCode;
import com.ninja.wangjia_backend.exception.ThrowUtils;
import com.ninja.wangjia_backend.model.dto.shop.ShopAddRequest;
import com.ninja.wangjia_backend.model.dto.shop.ShopQueryRequest;
import com.ninja.wangjia_backend.model.dto.shop.ShopUpdateRequest;
import com.ninja.wangjia_backend.model.entity.Shop;
import com.ninja.wangjia_backend.service.ShopService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/shop")
public class ShopController {
    @Resource
    private ShopService shopService;
    /*
    商品的增删改查
    * */
    @PostMapping("/add")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<Long> addShop(@RequestBody ShopAddRequest shopAddRequest) {
        return ResultUtils.success(shopService.addShop(shopAddRequest));
    }
    @PostMapping("/update")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<Boolean> updateShop(@RequestBody ShopUpdateRequest shopUpdateRequest) {
        return ResultUtils.success(shopService.updateShop(shopUpdateRequest));
    }
    @PostMapping("/delete")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<Boolean> deleteShop(@RequestBody DeleteRequest deleteRequest) {
        return ResultUtils.success(shopService.removeById(deleteRequest.getId()));
    }
    @PostMapping("/list/page")
    @AuthCheck(mustRole = "user")
    public BaseResponse<Page<Shop>> listShopByPage(@RequestBody ShopQueryRequest shopQueryRequest) {
        ThrowUtils.throwIf(shopQueryRequest == null, ErrorCode.PARAMS_ERROR);
        return ResultUtils.success(shopService.page(new Page<>(shopQueryRequest.getCurrent(), shopQueryRequest.getPageSize()), shopService.getQueryWrapper(shopQueryRequest)));
    }
    @PostMapping("/list")
    @AuthCheck(mustRole = "user")
    public BaseResponse<List<Shop>> listShop() {
        return ResultUtils.success(shopService.list());
    }
    @GetMapping("/increase/shopNum")
    @AuthCheck(mustRole = "user")
    public BaseResponse<Boolean> increaseShopNum(Long shopId, Integer num) {
        Shop shop = shopService.getById(shopId);
        ThrowUtils.throwIf(shop == null, ErrorCode.NOT_FOUND_ERROR, "商品不存在");
        ThrowUtils.throwIf(num < 0, ErrorCode.OPERATION_ERROR, "入库数量不能小于0");
        shop.setNum(shop.getNum() + num);
        return ResultUtils.success(shopService.updateById(shop));
    }

}
