package com.ninja.wangjia_backend.model.dto.shop;

import lombok.Data;

import java.io.Serializable;

@Data
public class ShopAddRequest implements Serializable {
    private String name;
    private Double price;
    private String type;
    private Integer num;
    private String info;
    private static final long serialVersionUID = 1L;
}
