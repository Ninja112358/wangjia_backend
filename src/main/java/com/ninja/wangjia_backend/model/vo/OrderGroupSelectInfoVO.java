package com.ninja.wangjia_backend.model.vo;

import com.ninja.wangjia_backend.model.entity.Order;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class OrderGroupSelectInfoVO implements Serializable {
    private Long id;
    private List<Order> orderList;
    private static final long serialVersionUID = 1L;
}
