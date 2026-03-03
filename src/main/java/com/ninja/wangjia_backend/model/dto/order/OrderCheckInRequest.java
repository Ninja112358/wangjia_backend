package com.ninja.wangjia_backend.model.dto.order;

import lombok.Data;

import java.io.Serializable;

@Data
public class OrderCheckInRequest implements Serializable {
    private String name;
    private String phone;
    private String idCard;
    private String cardType;
    private String orderInfo;
    private Integer customType;
    private String roomId;
    private Double roomPrice;
    private String roomType;
    private Double pay;



    private static final long serialVersionUID = 1L;
}
