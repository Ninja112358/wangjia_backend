package com.ninja.wangjia_backend.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 浏览器指纹信息
 * @TableName fingerprint
 */
@TableName(value ="fingerprint")
@Data
public class Fingerprint implements Serializable {
    /**
     * 指纹id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 指纹信息
     */
    private String fingerprint;

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