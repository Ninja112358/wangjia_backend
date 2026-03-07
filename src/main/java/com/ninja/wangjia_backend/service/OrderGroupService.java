package com.ninja.wangjia_backend.service;

import com.ninja.wangjia_backend.model.entity.OrderGroup;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author Administrator
* @description 针对表【order_group(订单组信息)】的数据库操作Service
* @createDate 2026-03-04 21:34:28
*/
public interface OrderGroupService extends IService<OrderGroup> {
    OrderGroup createOrderGroup();
}
