package com.ninja.wangjia_backend.service.impl;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ninja.wangjia_backend.exception.ErrorCode;
import com.ninja.wangjia_backend.exception.ThrowUtils;
import com.ninja.wangjia_backend.model.dto.shop.ShopAddRequest;
import com.ninja.wangjia_backend.model.dto.shop.ShopQueryRequest;
import com.ninja.wangjia_backend.model.dto.shop.ShopUpdateRequest;
import com.ninja.wangjia_backend.model.entity.RoomType;
import com.ninja.wangjia_backend.model.entity.Shop;
import com.ninja.wangjia_backend.service.ShopService;
import com.ninja.wangjia_backend.mapper.ShopMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
* @author Administrator
* @description 针对表【shop(商品信息)】的数据库操作Service实现
* @createDate 2026-03-16 10:57:19
*/
@Service
public class ShopServiceImpl extends ServiceImpl<ShopMapper, Shop>
    implements ShopService{

    @Override
    public Long addShop(ShopAddRequest shopAddRequest) {
        ThrowUtils.throwIf(shopAddRequest == null, ErrorCode.PARAMS_ERROR,"参数为空");
        //如果房间类型已存在
        long count = this.count(new QueryWrapper<Shop>().eq("name", shopAddRequest.getName()));
        ThrowUtils.throwIf(count > 0, ErrorCode.OPERATION_ERROR,"商品已存在");

        Shop shop = new Shop();
        BeanUtils.copyProperties(shopAddRequest, shop);
        ThrowUtils.throwIf(!this.save(shop), ErrorCode.OPERATION_ERROR,"添加商品失败");
        return shop.getId();
    }

    @Override
    public Boolean updateShop(ShopUpdateRequest shopUpdateRequest) {
        ThrowUtils.throwIf(shopUpdateRequest == null, ErrorCode.PARAMS_ERROR,"参数为空");
        Shop shop = new Shop();
        BeanUtils.copyProperties(shopUpdateRequest, shop);
        boolean result = this.updateById(shop);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR,"更新商品失败");
        return true;
    }

    @Override
    public Wrapper<Shop> getQueryWrapper(ShopQueryRequest shopQueryRequest) {
        ThrowUtils.throwIf(shopQueryRequest == null, ErrorCode.PARAMS_ERROR);
        Long id = shopQueryRequest.getId();
        String name = shopQueryRequest.getName();
        Double price = shopQueryRequest.getPrice();
        String type = shopQueryRequest.getType();
        Integer num = shopQueryRequest.getNum();
        String info = shopQueryRequest.getInfo();
        String sortField = shopQueryRequest.getSortField();
        String sortOrder = shopQueryRequest.getSortOrder();

        QueryWrapper<Shop> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(ObjUtil.isNotNull(id), "id", id);
        queryWrapper.like(StrUtil.isNotBlank(name), "name", name);
        queryWrapper.like(ObjUtil.isNotNull(price), "price", price);
        queryWrapper.like(StrUtil.isNotBlank(type), "type", type);
        queryWrapper.eq(ObjUtil.isNotNull(num), "num", num);
        queryWrapper.like(StrUtil.isNotBlank(info), "info", info);
        queryWrapper.orderBy(ObjUtil.isNotNull(sortField), sortOrder.equals("ascend"), sortField);
        return queryWrapper;
    }

}




