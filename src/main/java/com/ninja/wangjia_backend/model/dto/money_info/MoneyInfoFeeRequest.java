package com.ninja.wangjia_backend.model.dto.money_info;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class MoneyInfoFeeRequest implements Serializable {
    private Long orderId;
    private Double money;
    private String payInfo;
    private Date payTime;
}
