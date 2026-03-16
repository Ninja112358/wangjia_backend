package com.ninja.wangjia_backend.model.dto.order;

import com.ninja.wangjia_backend.common.PageRequest;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class OrderQueryRequest extends PageRequest implements Serializable {
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
    private Date startTime;
    private Date endTime;
    private String orderInfo;
    private Date createTime;
    private Date updateTime;
    private static final long serialVersionUID = 1L;
}
