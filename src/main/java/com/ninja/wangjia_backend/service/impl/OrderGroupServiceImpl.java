package com.ninja.wangjia_backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ninja.wangjia_backend.model.entity.OrderGroup;
import com.ninja.wangjia_backend.service.OrderGroupService;
import com.ninja.wangjia_backend.mapper.OrderGroupMapper;
import org.springframework.stereotype.Service;

/**
* @author Administrator
* @description 针对表【order_group(订单组信息)】的数据库操作Service实现
* @createDate 2026-03-04 21:34:28
*/
@Service
public class OrderGroupServiceImpl extends ServiceImpl<OrderGroupMapper, OrderGroup>
    implements OrderGroupService{

    @Override
    public OrderGroup createOrderGroup() {
        OrderGroup orderGroup = new OrderGroup();
        this.save(orderGroup);
        return orderGroup;
    }
}




