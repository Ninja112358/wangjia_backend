package com.ninja.wangjia_backend.model.dto.shop;

import com.ninja.wangjia_backend.common.PageRequest;
import lombok.Data;

import java.io.Serializable;

@Data
public class ShopQueryRequest extends PageRequest implements Serializable {
    private Long id;
    private String name;
    private Double price;
    private String type;
    private Integer num;
    private String info;
    private static final long serialVersionUID = 1L;
}
