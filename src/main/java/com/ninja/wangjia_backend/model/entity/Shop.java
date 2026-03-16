package com.ninja.wangjia_backend.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * 商品信息
 * @TableName shop
 */
@TableName(value ="shop")
@Data
public class Shop implements Serializable {
    /**
     * 商品id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 商品名字
     */
    private String name;

    /**
     * 商品价格
     */
    private Double price;

    /**
     * 商品类型
     */
    private String type;

    /**
     * 商品库存数量
     */
    private Integer num;

    /**
     * 商品备注
     */
    private String info;

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