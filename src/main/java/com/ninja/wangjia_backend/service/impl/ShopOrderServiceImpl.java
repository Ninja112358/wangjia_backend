package com.ninja.wangjia_backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ninja.wangjia_backend.model.entity.ShopOrder;
import com.ninja.wangjia_backend.service.ShopOrderService;
import com.ninja.wangjia_backend.mapper.ShopOrderMapper;
import org.springframework.stereotype.Service;

/**
* @author Administrator
* @description 针对表【shop_order(商品订单信息)】的数据库操作Service实现
* @createDate 2026-03-16 10:59:02
*/
@Service
public class ShopOrderServiceImpl extends ServiceImpl<ShopOrderMapper, ShopOrder>
    implements ShopOrderService{

}




