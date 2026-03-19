# 旺佳酒店管理系统 - 后端服务

## 项目简介

这是一个酒店管理系统后端，主要用于酒店的日常运营管理。系统支持房间管理、订单处理、商品销售等核心功能，适合中小型酒店使用。

## 技术选型

- **后端框架**: Spring Boot 2.7.6
- **ORM 框架**: MyBatis-Plus 3.5.9  
- **数据库**: MySQL + Redis
- **接口文档**: Knife4j
- **对象存储**: 腾讯云 COS
- **定时任务**: Quartz

## 快速启动

### 前置条件
- JDK 1.8
- Maven
- MySQL 5.7+
- Redis

### 配置步骤

**1. 导入数据库**

执行 `sql/create_table.sql` 和 `sql/quartz.sql` 创建数据库和表结构。

**2. 修改配置**

在 `application.yml` 中修改数据库连接信息:
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/wangjia_hotel
    username: root
    password: 你的密码
  redis:
    host: localhost
    port: 6379
```

**3. 启动项目**
```bash
mvn spring-boot:run
```

启动成功后访问：http://localhost:8110/api/doc.html

详细接口文档请查看：[API 接口文档](docs/API.md)

## 主要功能

### 房间管理
- 房间状态实时更新（空房、在住、维修、脏房）
- 房型和价格配置
- 房间信息查询和修改

### 订单管理  
- 入住登记和退房办理
- 支持散客和团队订单
- 订单自动扣费（每天下午 2:30）
- 联房、团队房等特殊处理

### 商品管理
- 酒店商品入库和销售
- 商品订单记录
- 支持挂账和现结两种付款方式

### 财务管理
- 收款记录统计
- 消费明细查询
- 房间消费和商品消费分类统计

### 其他功能
- 文件上传（最大 20MB）
- 浏览器指纹识别
- 用户权限管理

## 目录结构

```
src/main/java/com/ninja/wangjia_backend/
├── controller/      # 接口控制器
├── service/         # 业务逻辑
├── mapper/          # 数据访问
├── model/           # 数据模型
│   ├── entity/     # 实体类
│   ├── dto/        # 数据传输对象
│   └── vo/         # 视图对象
├── config/          # 配置类
├── annotation/      # 自定义注解
├── aop/            # 切面
├── exception/       # 异常处理
└── quartz/          # 定时任务
```

## 数据库表说明

| 表名 | 用途 |
|------|------|
| user | 系统用户 |
| room | 房间信息 |
| room_type | 房型配置 |
| order | 订单记录 |
| order_group | 订单组（关联多个订单）|
| money_info | 金额流水 |
| shop | 商品信息 |
| shop_order | 商品销售记录 |
| fingerprint | 浏览器指纹 |

## 开发相关

### 添加新接口

1. 在对应的 Controller 中添加方法
2. Service 层编写业务逻辑
3. Mapper 层处理数据访问
4. 使用 `@AuthCheck` 注解控制权限

### 权限控制

```java
// 需要管理员权限
@AuthCheck(mustRole = "admin")
@PostMapping("/delete")
public BaseResponse delete(...) {
    // ...
}
```

### 对象存储

文件上传已集成腾讯云 COS，使用 `CosManager` 工具类即可。

## 部署

### 本地运行
```bash
mvn clean package
java -jar target/wangjia_backend-0.0.1-SNAPSHOT.jar
```

### Docker 部署
```bash
docker build -t wangjia_backend .
docker run -d -p 8110:8110 wangjia_backend
```

## 注意事项

1. 首次运行需要先执行 SQL 脚本建表
2. Redis 和 MySQL 要提前启动
3. 文件上传大小限制在 application.yml 中配置
4. 定时任务基于 Quartz，依赖数据库存储

## 接口测试

推荐使用 Knife4j 的在线调试功能，比 Postman 方便，可以直接看到参数说明和响应示例。

---

有问题可以查看代码注释或联系开发者。