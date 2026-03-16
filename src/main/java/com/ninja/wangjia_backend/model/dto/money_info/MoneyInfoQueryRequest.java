package com.ninja.wangjia_backend.model.dto.money_info;

import com.ninja.wangjia_backend.common.PageRequest;
import lombok.Data;

import java.io.Serializable;

@Data
public class MoneyInfoQueryRequest extends PageRequest implements Serializable {
    private Long id;
    private Long orderId;
    private String moneyType;
    private String roomId;
    private String operator;
    private String payInfo;
    private static final long serialVersionUID = 1L;
}
