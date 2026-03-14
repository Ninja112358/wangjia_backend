package com.ninja.wangjia_backend.model.dto.money_info;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MoneyInfoFeeRequest implements Serializable {
    private Long orderId;
    private Double money;
    private String payInfo;
    private Date payTime;
}
