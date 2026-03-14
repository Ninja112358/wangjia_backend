package com.ninja.wangjia_backend.model.dto.order;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class OrderUpdateRequest implements Serializable {
    private Long id;
    private String name;
    private String phone;
    private String idCard;
    private String cardType;
    private String roomId;
    private String roomType;
    private Long orderGroupId;
    private Double pay;
    private Double restMoney;
    private Double consume;
    private Double shopConsume;
    private Double roomPrice;
    private Integer customType; //0散客,1团队
    private Integer orderState; //0未结,1已结
    private String orderInfo;
    private static final long serialVersionUID = 1L;
}
