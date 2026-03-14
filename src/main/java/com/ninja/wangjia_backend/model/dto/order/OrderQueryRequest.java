package com.ninja.wangjia_backend.model.dto.order;

import com.ninja.wangjia_backend.common.PageRequest;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/*
create table if not exists `order`
(
    id              bigint auto_increment                       comment '订单id' primary key,
    name            varchar(200)                                not null comment '顾客名称',
    phone           varchar(20)                                 not null comment '联系电话',
    idCard          varchar(20)                                 not null comment '顾客身份证号',
    cardType        varchar(20)                                 not null comment '证件类型',
    pay             decimal(10,2)   default 0                   not null comment '付款金额',
    restMoney       decimal(10,2)   default 0                   not null comment '剩余金额',
    consume         decimal(10,2)   default 0                   not null comment '房间消费金额',
    shopConsume     decimal(10,2)   default 0                   not null comment '商品消费金额',
    roomId          varchar(20)                                 not null comment '房间id',
    roomType        varchar(20)                                 not null comment '房间类型',
    roomPrice       decimal(10,2)   default 0                   not null comment '房间价格',
    orderGroupId    bigint                                      not null comment '订单组的id',
    customType      int             default 0                   not null comment '顾客类型(0散客,1团队)',
    orderInfo       varchar(1000)                               null comment '订单备注信息',
    orderState      int             default 0                   not null comment '订单状态(0未结,1已结)',
    startTime       datetime        default CURRENT_TIMESTAMP   not null comment '入住时间',
    endTime         datetime                                    null comment '退房时间',
    createTime      datetime        default CURRENT_TIMESTAMP   not null comment '创建时间',
    updateTime      datetime        default CURRENT_TIMESTAMP   not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete        tinyint         default 0                   not null comment '是否删除',
    INDEX idx_roomId(roomId),
    INDEX idx_orderGroupId(orderGroupId),
    INDEX idx_name(name),
    INDEX idx_phone(phone),
    INDEX idx_IDCard(IDCard)
) comment '订单信息' collate = utf8mb4_unicode_ci;
* */
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
