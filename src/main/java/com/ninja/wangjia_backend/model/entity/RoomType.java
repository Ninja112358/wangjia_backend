package com.ninja.wangjia_backend.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import lombok.ToString;

/**
 * 房间类型
 * @TableName room_type
 */
@TableName(value ="room_type")
@Data
@ToString
public class RoomType implements Serializable {
    /**
     * 房间类型id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 房间类型名称
     */
    private String name;

    /**
     * 房间价格
     */
    private Double price;

    /**
     * 房间人数
     */
    private Integer peopleNum;

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