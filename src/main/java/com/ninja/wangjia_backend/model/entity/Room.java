package com.ninja.wangjia_backend.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 房间
 * @TableName room
 */
@TableName(value ="room")
@Data
public class Room implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 房间号
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
     * 房间状态:0：正常，1：有客，2：维修，3：空打扫，4.住宿打扫，5.锁房
     */
    private Integer roomState;

    /**
     * 房间当前订单id
     */
    private Long orderId;

    /**
     * 房间楼层
     */
    private Integer roomFloor;

    /**
     * 房间住客
     */
    private String roomCustom;

    /**
     * 房间人数
     */
    private Integer roomPeopleNum;

    /**
     * 房间备注
     */
    private String roomInfo;

    /**
     * 房间是否带窗
     */
    private String isWindow;

    /**
     * 房间是否是团队房间
     */
    private Boolean isTeam;

    /**
     * 房间是否是联房
     */
    private Boolean isContact;

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