package com.ninja.wangjia_backend.model.dto.order;

import lombok.Data;

import java.io.Serializable;

@Data
public class OrderChangeRoomPriceRequest implements Serializable {
    private Long orderId;
    private Double roomPrice;
    private String payInfo;
    private static final long serialVersionUID = 1L;
}
