package com.ninja.wangjia_backend.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * 金额信息
 * @TableName money_info
 */
@TableName(value ="money_info")
@Data
public class MoneyInfo implements Serializable {
    /**
     * 金额信息id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 订单id
     */
    private Long orderId;

    /**
     * 营业项目(收款,扣费)
     */
    private String moneyType;

    /**
     * 金额
     */
    private Double money;

    /**
     * 房间id
     */
    private String roomId;
    /**
     * 操作员
     */
    private String operator;

    /**
     * 支付信息
     */
    private String payInfo;

    /**
     * 支付时间
     */
    private Date payTime;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除
     */
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}