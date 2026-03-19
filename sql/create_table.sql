create database if not exists wangjia_hotel;
use wangjia_hotel;

create table if not exists user(
   id              bigint auto_increment                       comment 'id' primary key,
   userAccount     varchar(256)                                not null comment '账号',
   userPassword    varchar(512)                                not null comment '密码',
   userName        varchar(256)                                null comment '用户昵称',
   userAvatar      varchar(1024)                               null comment '用户头像',
   userProfile     varchar(512)                                null comment '用户简介',
   userRole        varchar(256)    default 'user'              not null comment '用户角色：user/admin',
   editTime        datetime        default CURRENT_TIMESTAMP   not null comment '编辑时间',
   createTime      datetime        default CURRENT_TIMESTAMP   not null comment '创建时间',
   updateTime      datetime        default CURRENT_TIMESTAMP   not null on update CURRENT_TIMESTAMP comment '更新时间',
   isDelete        tinyint         default 0                   not null comment '是否删除',
   INDEX idx_userAccount(userAccount),
   INDEX idx_userName(userName)
) comment '用户' collate = utf8mb4_unicode_ci;

create table if not exists room(
    id              bigint auto_increment                       comment 'id' primary key,
    roomId          varchar(256)                                not null comment '房间号',
    roomType        varchar(256)                                null comment '房间类型',
    roomPrice       decimal(10,2)                               null comment '房间价格',
    roomState       int    default 0                            null comment '房间状态:0：空房，1：在住，2：维修，3：空脏，4.在住脏，5.锁房',
    orderId         bigint                                      null comment '房间当前订单id',
    roomFloor       int                                         null comment '房间楼层',
    roomCustom      varchar(256)                                null comment '房间住客',
    roomPeopleNum   int default 0                               null comment '房间人数',
    roomInfo        varchar(256)                                null comment '房间备注',
    orderInfo       varchar(256)                                null comment '房间订单的信息',
    isWindow        varchar(256)    default '有窗'               null comment '房间是否带窗',
    isTeam          boolean         default false               null comment '房间是否是团队房间',
    isContact       boolean         default false               null comment '房间是否是联房',
    createTime      datetime        default CURRENT_TIMESTAMP   not null comment '创建时间',
    updateTime      datetime        default CURRENT_TIMESTAMP   not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete        tinyint         default 0                   not null comment '是否删除',
    INDEX idx_roomId(roomId)
) comment '房间' collate = utf8mb4_unicode_ci;

create table if not exists room_type(
    id              bigint auto_increment                       comment '房间类型id' primary key,
    name            varchar(20)                                 not null comment '房间类型名称',
    price           float                                       not null comment '房间价格',
    peopleNum       int                                         not null comment '房间人数',
    createTime      datetime        default CURRENT_TIMESTAMP   not null comment '创建时间',
    updateTime      datetime        default CURRENT_TIMESTAMP   not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete        tinyint         default 0                   not null comment '是否删除'
) comment '房间类型' collate = utf8mb4_unicode_ci;

drop table if exists `order`;
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
    deductState     int             default 0                   not null comment '下午两点半的时候是否该扣费的状态(0无需扣费,1需要扣费,2需要下一次调用时候扣费(也就是隔一天扣费))',
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
# 在order表中添加一个字段deductState     int             default 0                   not null comment '下午两点半的时候是否该扣费的状态(0无需扣费,1需要扣费,2需要下一次调用时候扣费(也就是隔一天扣费))',


drop table if exists money_info;
create table if not exists money_info(
    id              bigint auto_increment                       comment '金额信息id' primary key,
    orderId         bigint                                      not null comment '订单id',
    moneyType       varchar(20)                                 not null comment '营业项目(收款,扣费)',
    money           decimal(10,2)                               not null comment '金额',
    roomId          varchar(20)                                 not null comment '房间id',
    operator        varchar(20)     default '系统'               not null comment '操作人',
    payInfo         varchar(1000)                               null comment '支付信息',
    payTime         datetime        default CURRENT_TIMESTAMP   not null comment '支付时间',
    createTime      datetime        default CURRENT_TIMESTAMP   not null comment '创建时间',
    updateTime      datetime        default CURRENT_TIMESTAMP   not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete        tinyint         default 0                   not null comment '是否删除',
    INDEX idx_roomId(roomId),
    INDEX idx_orderId(orderId)
) comment '金额信息' collate = utf8mb4_unicode_ci;

create table if not exists order_group(
    id              bigint auto_increment                   comment '订单组id' primary key,
    state           int             default 0               comment '订单组状态(0未完成,1已完成)',
    createTime      datetime        default CURRENT_TIMESTAMP   not null comment '创建时间',
    updateTime      datetime        default CURRENT_TIMESTAMP   not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete        tinyint         default 0                   not null comment '是否删除'
) comment '订单组信息' collate = utf8mb4_unicode_ci;

create table if not exists shop(
    id              bigint auto_increment                   comment '商品id' primary key,
    name            varchar(200)                            comment '商品名字',
    price           decimal(10,2)                           comment '商品价格',
    type            varchar(200)                            comment '商品类型',
    num             int                                     comment '商品库存数量',
    info            varchar(2000)                           comment '商品备注',
    createTime      datetime        default CURRENT_TIMESTAMP   not null comment '创建时间',
    updateTime      datetime        default CURRENT_TIMESTAMP   not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete        tinyint         default 0                   not null comment '是否删除'
) comment '商品信息' collate = utf8mb4_unicode_ci;

create table if not exists shop_order(
    id              bigint auto_increment                       comment '商品订单id' primary key,
    shopName        varchar(200)                                comment '商品名字',
    shopPrice       decimal(10,2)                               comment '商品单价',
    customType      int                                         comment '顾客类型(0:在住,1:外来)',
    payType         int                                         comment '付款方式(0:入账,1:付现)',
    roomId          varchar(200)                                comment '房间号',
    shopType        varchar(200)                                comment '商品类型',
    shopNum         int                                         comment '商品销售数量',
    shopInfo        varchar(2000)                               comment '订单备注',
    createTime      datetime        default CURRENT_TIMESTAMP   not null comment '创建时间',
    updateTime      datetime        default CURRENT_TIMESTAMP   not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete        tinyint         default 0                   not null comment '是否删除',
    INDEX idx_roomId(roomId),
    INDEX idx_shopName(shopName)
) comment '商品订单信息' collate = utf8mb4_unicode_ci;

create table if not exists fingerprint(
    id              bigint auto_increment                       comment '指纹id' primary key,
    fingerprint     varchar(2000)                               unique not null comment '指纹信息',
    createTime      datetime        default CURRENT_TIMESTAMP   not null comment '创建时间',
    updateTime      datetime        default CURRENT_TIMESTAMP   not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete        tinyint         default 0                   not null comment '是否删除'
) comment '浏览器指纹信息' collate = utf8mb4_unicode_ci;