package com.ninja.wangjia_backend.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * 商品订单信息
 * @TableName shop_order
 */
@TableName(value ="shop_order")
@Data
public class ShopOrder implements Serializable {
    /**
     * 商品订单id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 商品名字
     */
    private String shopName;

    /**
     * 商品单价
     */
    private BigDecimal shopPrice;

    /**
     * 顾客类型(0:在住,1:外来)
     */
    private Integer customType;

    /**
     * 付款方式(0:入账,1:付现)
     */
    private Integer payType;

    /**
     * 房间号
     */
    private String roomId;

    /**
     * 商品类型
     */
    private String shopType;

    /**
     * 商品销售数量
     */
    private Integer shopNum;

    /**
     * 订单备注
     */
    private String shopInfo;

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