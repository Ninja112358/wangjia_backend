# 接口文档

项目启动后访问 http://localhost:8110/api/doc.html 查看在线接口文档。

以下是主要接口的说明：

---

## 用户接口 (/user)

### 用户注册
- **接口**: `POST /api/user/register`
- **权限**: 公开
- **参数**: 
  - userAccount: 账号
  - userPassword: 密码
  - checkPassword: 确认密码

### 用户登录
- **接口**: `POST /api/user/login`
- **权限**: 公开
- **参数**:
  - userAccount: 账号
  - userPassword: 密码
- **返回**: 登录用户信息（包含 token）

### 获取当前登录用户
- **接口**: `GET /api/user/get/login`
- **权限**: 需要登录
- **返回**: 当前登录用户信息

### 用户注销
- **接口**: `GET /api/user/logout`
- **权限**: 需要登录

### 创建用户
- **接口**: `POST /api/user/add`
- **权限**: 管理员
- **参数**: UserAddRequest 对象

### 根据 ID 获取用户
- **接口**: `GET /api/user/get`
- **权限**: 管理员
- **参数**: id (用户 ID)

### 获取用户 VO
- **接口**: `GET /api/user/get/vo`
- **权限**: 公开
- **参数**: id (用户 ID)

### 删除用户
- **接口**: `POST /api/user/delete`
- **权限**: 管理员
- **参数**: DeleteRequest 对象（包含 id）

### 更新用户
- **接口**: `POST /api/user/update`
- **权限**: 管理员
- **参数**: UserUpdateRequest 对象

### 分页获取用户列表
- **接口**: `POST /api/user/list/page/vo`
- **权限**: 管理员
- **参数**: UserQueryRequest 对象（包含 current、pageSize 等）

---

## 房间接口 (/room)

### 设置房间状态
- **接口**: `POST /api/room/set/state`
- **权限**: 普通用户
- **参数**: RoomSetStateRequest 对象（包含 id、roomState）
- **房间状态**: 0-空房，1-在住，2-维修，3-空脏，4-在住脏，5-锁房

### 添加房间
- **接口**: `POST /api/room/add`
- **权限**: 管理员
- **参数**: RoomAddRequest 对象

### 删除房间
- **接口**: `POST /api/room/delete`
- **权限**: 管理员
- **参数**: DeleteRequest 对象

### 更新房间
- **接口**: `POST /api/room/update`
- **权限**: 管理员
- **参数**: RoomUpdateRequest 对象

### 分页获取房间列表
- **接口**: `POST /api/room/list/page`
- **权限**: 管理员
- **参数**: RoomQueryRequest 对象

### 获取所有房间
- **接口**: `POST /api/room/list`
- **权限**: 普通用户
- **返回**: 房间列表

---

## 房型接口 (/room_type)

### 添加房型
- **接口**: `POST /api/room_type/add`
- **权限**: 管理员
- **参数**: RoomTypeAddRequest 对象

### 分页获取房型列表
- **接口**: `POST /api/room_type/list/page`
- **权限**: 管理员
- **参数**: RoomTypeQueryRequest 对象

### 获取所有房型
- **接口**: `POST /api/room_type/list`
- **权限**: 普通用户

### 删除房型
- **接口**: `POST /api/room_type/delete`
- **权限**: 管理员
- **参数**: DeleteRequest 对象

### 更新房型
- **接口**: `POST /api/room_type/update`
- **权限**: 管理员
- **参数**: RoomTypeUpdateRequest 对象

---

## 订单接口 (/order)

### 入住登记
- **接口**: `POST /api/order/checkin`
- **权限**: 普通用户
- **参数**: OrderCheckInRequest 对象
  - name: 顾客姓名
  - phone: 联系电话
  - idCard: 身份证号
  - cardType: 证件类型
  - roomId: 房间号
  - roomType: 房型
  - roomPrice: 房价
  - pay: 付款金额（可选）
  - customType: 顾客类型（0-散客，1-团队）
  - orderInfo: 备注信息

### 退房
- **接口**: `POST /api/order/checkout`
- **权限**: 普通用户
- **参数**: orderId (订单 ID)

### 取消退房
- **接口**: `POST /api/order/checkout/cancel`
- **权限**: 普通用户
- **参数**: orderId (订单 ID)

### 查询订单组数据
- **接口**: `POST /api/order/list/orderGroupData`
- **权限**: 普通用户
- **参数**: orderId (订单 ID)
- **返回**: 该订单组下的所有订单

### 修改房价
- **接口**: `POST /api/order/room_price/change`
- **权限**: 普通用户
- **参数**: OrderChangeRoomPriceRequest 对象
  - orderId: 订单 ID
  - roomPrice: 新房价
  - payInfo: 备注信息

### 搜索订单
- **接口**: `POST /api/order/search`
- **权限**: 普通用户
- **参数**: input (搜索关键字，支持姓名、电话、身份证号)
- **返回**: 匹配的订单列表

### 换房
- **接口**: `POST /api/order/room/change`
- **权限**: 普通用户
- **参数**: OrderChangeRoomRequest 对象
  - orderId: 订单 ID
  - roomId: 新房间号
  - roomPrice: 新房价
  - payInfo: 备注信息

### 联房操作
- **接口**: `POST /api/order/contact`
- **权限**: 普通用户
- **参数**: OrderContactRequest 对象
  - orderGroupId: 主订单组 ID
  - orderGroupSelectInfoList: 要合并的订单组信息

### 分页获取订单列表
- **接口**: `POST /api/order/list/page`
- **权限**: 普通用户
- **参数**: OrderQueryRequest 对象

### 更新订单
- **接口**: `POST /api/order/update`
- **权限**: 管理员
- **参数**: OrderUpdateRequest 对象

### 查询订单提醒状态
- **接口**: `GET /api/order/state/remind`
- **权限**: 普通用户
- **参数**: orderId (订单 ID)
- **返回**: 是否需要提醒（余额不足时返回 true）

---

## 金额信息接口 (/money_info)

### 收款
- **接口**: `POST /api/money_info/pay`
- **权限**: 普通用户
- **参数**: MoneyInfoFeeRequest 对象
  - orderId: 订单 ID
  - money: 金额
  - moneyType: 营业项目类型
  - payInfo: 支付信息
  - payTime: 支付时间

### 扣费
- **接口**: `POST /api/money_info/deduct`
- **权限**: 普通用户
- **参数**: MoneyInfoFeeRequest 对象

### 根据订单 ID 查询金额信息
- **接口**: `POST /api/money_info/list/orderId`
- **权限**: 普通用户
- **参数**: orderId (订单 ID)

### 查询订单组的金额信息
- **接口**: `POST /api/money_info/list/group/orderId`
- **权限**: 普通用户
- **参数**: orderId (订单 ID)

### 小吧入账
- **接口**: `POST /api/money_info/deduct/shop`
- **权限**: 普通用户
- **参数**: ShopEnterOrderRequest 对象
  - orderId: 订单 ID
  - shopList: 商品列表（包含商品 ID、数量等）

### 分页获取金额信息列表
- **接口**: `POST /api/money_info/list/page`
- **权限**: 普通用户
- **参数**: MoneyInfoQueryRequest 对象

### 更新金额信息
- **接口**: `POST /api/money_info/update`
- **权限**: 管理员
- **参数**: MoneyInfoUpdateRequest 对象

### 删除金额信息
- **接口**: `POST /api/money_info/delete`
- **权限**: 管理员
- **参数**: DeleteRequest 对象

---

## 商品接口 (/shop)

### 添加商品
- **接口**: `POST /api/shop/add`
- **权限**: 管理员
- **参数**: ShopAddRequest 对象

### 更新商品
- **接口**: `POST /api/shop/update`
- **权限**: 管理员
- **参数**: ShopUpdateRequest 对象

### 删除商品
- **接口**: `POST /api/shop/delete`
- **权限**: 管理员
- **参数**: DeleteRequest 对象

### 分页获取商品列表
- **接口**: `POST /api/shop/list/page`
- **权限**: 普通用户
- **参数**: ShopQueryRequest 对象

### 获取所有商品
- **接口**: `POST /api/shop/list`
- **权限**: 普通用户

### 增加商品库存
- **接口**: `GET /api/shop/increase/shopNum`
- **权限**: 普通用户
- **参数**: 
  - shopId: 商品 ID
  - num: 入库数量

---

## 文件接口 (/file)

### 上传文件
- **接口**: `POST /api/file/upload`
- **权限**: 管理员
- **参数**: file (MultipartFile)
- **限制**: 单文件最大 20MB
- **返回**: 文件路径

### 下载文件
- **接口**: `GET /api/file/download/`
- **权限**: 管理员
- **参数**: filepath (文件路径)
- **返回**: 文件流

---

## 定时任务接口 (/job)

JobController 包含了定时任务的管理接口，主要用于 Quartz 任务的配置和执行。具体接口请参考在线文档。

---

## 指纹接口 (/finger_print)

用于浏览器指纹信息的采集和管理，支持安全验证功能。

---

## 权限说明

- **公开接口**: 无需登录即可访问（如注册、登录）
- **普通用户权限**: 需要登录，角色为 "user" 或 "admin"
- **管理员权限**: 需要登录且角色为 "admin"

使用 `@AuthCheck(mustRole = "...")` 注解控制接口权限。

## 通用响应格式

所有接口统一返回 BaseResponse 对象：

```json
{
  "code": 0,          // 状态码，0 表示成功
  "data": {},         // 返回数据
  "message": "success" // 提示信息
}
```

## 错误码说明

常见错误码：
- 0: 成功
- 400: 请求参数错误
- 401: 未授权
- 403: 禁止访问
- 404: 资源不存在
- 500: 系统内部错误

详细错误码定义见 `ErrorCode.java`
