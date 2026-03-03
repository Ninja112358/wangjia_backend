package com.ninja.wangjia_backend.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;
import lombok.ToString;

/**
 * 订单信息
 * @TableName order
 */
@TableName(value ="`order`")
@Data
@ToString
public class Order implements Serializable {
    /**
     * 订单id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 顾客名称
     */
    private String name;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 顾客身份证号
     */
    private String idCard;
    /**
     * 证件类型
     */
    private String cardType;

    /**
     * 付款金额
     */
    private Double pay;

    /**
     * 剩余金额
     */
    private Double restMoney;

    /**
     * 消费金额
     */
    private Double consume;

    /**
     * 房间id
     */
    private String roomId;

    /**
     * 房间类型
     */
    private String roomType;

    /**
     * 房间价格
     */
    private Double roomPrice;

    /**
     * 订单组的id
     */
    private Long orderGroupId;

    /**
     * 顾客类型(0散客,1团队)
     */
    private Integer customType;

    /**
     * 订单备注信息
     */
    private String orderInfo;

    /**
     * 订单状态(0未结,1已结)
     */
    private Integer orderState;

    /**
     * 退房时间
     */
    private Date endTime;

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