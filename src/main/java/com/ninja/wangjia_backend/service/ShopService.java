package com.ninja.wangjia_backend.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.ninja.wangjia_backend.model.dto.shop.ShopAddRequest;
import com.ninja.wangjia_backend.model.dto.shop.ShopQueryRequest;
import com.ninja.wangjia_backend.model.dto.shop.ShopUpdateRequest;
import com.ninja.wangjia_backend.model.entity.Shop;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author Administrator
* @description 针对表【shop(商品信息)】的数据库操作Service
* @createDate 2026-03-16 10:57:19
*/
public interface ShopService extends IService<Shop> {

    Long addShop(ShopAddRequest shopAddRequest);

    Boolean updateShop(ShopUpdateRequest shopUpdateRequest);

    Wrapper<Shop> getQueryWrapper(ShopQueryRequest shopQueryRequest);
}
