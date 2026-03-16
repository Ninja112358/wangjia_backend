package com.ninja.wangjia_backend.model.dto.shop;

import com.ninja.wangjia_backend.model.entity.Shop;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ShopEnterOrderRequest implements Serializable {
    private Long orderId;
    private List<Shop> shopList;
    private static final long serialVersionUID = 1L;
}
