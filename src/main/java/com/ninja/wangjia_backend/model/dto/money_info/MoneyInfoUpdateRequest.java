package com.ninja.wangjia_backend.model.dto.money_info;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class MoneyInfoUpdateRequest implements Serializable {
    private Long id;
    private String moneyType;
    private Double money;
    private String payInfo;
    private Date payTime;
    private static final long serialVersionUID = 1L;
}
