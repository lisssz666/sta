# 裁判订单管理功能接口清单

## 功能概述

裁判订单管理功能提供了对裁判订单信息的完整CRUD操作，包括创建、删除、修改、查询列表和详情等功能。接口设计遵循RESTful风格，使用统一的响应格式。

## 接口清单

### 1. 创建裁判订单

**接口地址**：`/referee/order/create`

**请求方法**：`POST`

**请求参数**：

| 参数名 | 类型 | 必填 | 描述 |
|-------|------|------|------|
| refereeId | Long | 是 | 裁判ID |
| contactName | String | 是 | 联系人姓名 |
| contactPhone | String | 是 | 联系人电话 |
| totalAmount | String | 是 | 订单金额 |
| gameId | Long | 否 | 比赛ID（如果提供，会自动查询比赛信息） |
| matchTime | String | 否 | 比赛时间（如果提供了gameId，此参数会被忽略） |
| remark | String | 否 | 备注信息 |
| matchInfo | String | 否 | 比赛信息（如果提供了gameId，此参数会被忽略） |
| matchLocation | String | 否 | 比赛地点（如果提供了gameId，此参数会被忽略） |
| scheduleLog | String | 否 | 裁判服务时间段（JSON数组），例如：[{"date": "2026-01-28", "time": ["16:00-18:00","18:00-20:00","20:00-22:00"]}, {"date": "2026-01-29", "time": ["16:00-18:00","18:00-20:00","20:00-22:00"]}] |

**请求示例**：

1. 使用比赛ID创建订单：
```
POST /referee/order/create?refereeId=1&contactName=张三&contactPhone=13800138000&totalAmount=100.00&gameId=1
```

2. 不使用比赛ID创建订单：
```
POST /referee/order/create?refereeId=1&contactName=张三&contactPhone=13800138000&totalAmount=100.00&matchInfo=A队 vs B队&matchTime=2026-01-22 14:00:00&matchLocation=体育馆
```

**响应示例**：
```json
{
  "code": 200,
  "msg": "成功",
  "data": {
    "id": 1,
    "orderNo": "RF20260121100000",
    "refereeId": 1,
    "refereeName": "张裁判",
    "refereeLevel": "国家级",
    "contactName": "张三",
    "contactPhone": "13800138000",
    "matchInfo": "A队 vs B队",
    "matchTime": "2026-01-22 14:00:00",
    "matchLocation": "体育馆",
    "totalAmount": "100.00",
    "remark": "",
    "status": 0,
    "createTime": "2026-01-21 10:00:00",
    "updateTime": "2026-01-21 10:00:00"
  }
}
```

### 2. 删除裁判订单

**接口地址**：`/referee/order/delete`

**请求方法**：`DELETE`

**请求参数**：
- `id`：订单ID（必填）

**响应示例**：
```json
{
  "code": 200,
  "msg": "订单已删除",
  "data": null
}
```

### 3. 修改裁判订单

**接口地址**：`/referee/order/update`

**请求方法**：`PUT`

**请求参数**：
```json
{
  "id": 1,                   // 订单ID（必填）
  "contactName": "新联系人姓名", // 联系人姓名（可选）
  "contactPhone": "新联系人电话", // 联系人电话（可选）
  "matchInfo": "新比赛信息",    // 比赛信息（可选）
  "matchTime": "新比赛时间",    // 比赛时间（可选）
  "matchLocation": "新比赛地点", // 比赛地点（可选）
  "remark": "新备注信息",       // 备注信息（可选）
  "status": 1                // 订单状态（可选）：0未付款 1已付款 2已取消 3已完成
}
```

**响应示例**：
```json
{
  "code": 200,
  "msg": "订单已更新",
  "data": null
}
```

### 4. 查询裁判订单列表

**接口地址**：`/referee/order/list`

**请求方法**：`GET`

**请求参数**：
- `refereeId`：裁判ID（可选）
- `contactPhone`：联系人电话（可选）
- `status`：订单状态（可选）：0未付款 1已付款 2已取消 3已完成

**响应示例**：
```json
{
  "code": 200,
  "msg": "成功",
  "data": [
    {
      "id": 1,
      "orderNo": "RF20260121100000",
      "refereeId": 1,
      "refereeName": "张裁判",
      "refereeLevel": "国家级",
      "contactName": "联系人姓名",
      "contactPhone": "联系人电话",
      "matchInfo": "比赛信息",
      "matchTime": "比赛时间",
      "matchLocation": "比赛地点",
      "totalAmount": "100.00",
      "remark": "备注信息",
      "status": 0,
      "createTime": "2026-01-21 10:00:00",
      "updateTime": "2026-01-21 10:00:00"
    },
    {
      "id": 2,
      "orderNo": "RF20260121100001",
      "refereeId": 2,
      "refereeName": "李裁判",
      "refereeLevel": "省级",
      "contactName": "联系人姓名2",
      "contactPhone": "联系人电话2",
      "matchInfo": "比赛信息2",
      "matchTime": "比赛时间2",
      "matchLocation": "比赛地点2",
      "totalAmount": "80.00",
      "remark": "备注信息2",
      "status": 1,
      "createTime": "2026-01-21 10:30:00",
      "updateTime": "2026-01-21 11:00:00"
    }
  ]
}
```

### 5. 查询裁判订单详情

**接口地址**：`/referee/order/detail`

**请求方法**：`GET`

**请求参数**：
- `id`：订单ID（必填）

**响应示例**：
```json
{
  "code": 200,
  "msg": "成功",
  "data": {
    "id": 1,
    "orderNo": "RF20260121100000",
    "refereeId": 1,
    "refereeName": "张裁判",
    "refereeLevel": "国家级",
    "contactName": "联系人姓名",
    "contactPhone": "联系人电话",
    "matchInfo": "比赛信息",
    "matchTime": "比赛时间",
    "matchLocation": "比赛地点",
    "totalAmount": "100.00",
    "remark": "备注信息",
    "status": 0,
    "createTime": "2026-01-21 10:00:00",
    "updateTime": "2026-01-21 10:00:00"
  }
}
```

## 数据库设计

### 裁判订单信息表（mall_referee_order）

```sql
CREATE TABLE `mall_referee_order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_no` varchar(50) DEFAULT NULL COMMENT '订单号',
  `referee_id` bigint(20) DEFAULT NULL COMMENT '裁判ID',
  `referee_name` varchar(100) DEFAULT NULL COMMENT '裁判姓名',
  `referee_level` varchar(50) DEFAULT NULL COMMENT '裁判等级',
  `contact_name` varchar(100) DEFAULT NULL COMMENT '联系人姓名',
  `contact_phone` varchar(20) DEFAULT NULL COMMENT '联系人电话',
  `match_info` varchar(500) DEFAULT NULL COMMENT '比赛信息',
  `match_time` varchar(100) DEFAULT NULL COMMENT '比赛时间',
  `match_location` varchar(500) DEFAULT NULL COMMENT '比赛地点',
  `total_amount` varchar(20) DEFAULT NULL COMMENT '订单金额',
  `remark` varchar(1000) DEFAULT NULL COMMENT '备注',
  `status` int(11) DEFAULT '0' COMMENT '订单状态：0未付款 1已付款 2已取消 3已完成',
  `transaction_id` varchar(100) DEFAULT NULL COMMENT '微信支付订单号',
  `pay_time` datetime DEFAULT NULL COMMENT '支付时间',
  `deleted` int(11) DEFAULT '0' COMMENT '逻辑删除：0未删 1已删',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='裁判订单信息表';
```

## 项目结构

```
src/main/java/com/ruoyi/mall/order/
├── controller/
│   ├── OrderController.java       # 普通订单控制器
│   └── RefereeOrderController.java # 裁判订单控制器
├── domain/
│   ├── MallOrder.java             # 普通订单实体类
│   ├── RefereeOrder.java          # 裁判订单实体类
│   └── vo/
│       ├── OrderDetailVO.java     # 普通订单详情VO
│       ├── OrderListVO.java       # 普通订单列表VO
│       └── RefereeOrderVO.java     # 裁判订单VO
├── mapper/
│   ├── MallOrderMapper.java       # 普通订单Mapper接口
│   └── RefereeOrderMapper.java     # 裁判订单Mapper接口
└── service/
    ├── MallOrderService.java      # 普通订单Service接口
    ├── RefereeOrderService.java    # 裁判订单Service接口
    └── impl/
        ├── MallOrderServiceImpl.java # 普通订单Service实现类
        └── RefereeOrderServiceImpl.java # 裁判订单Service实现类
```

## 功能特点

1. **高效简洁的接口设计**：遵循RESTful风格，接口命名规范，参数清晰
2. **完整的CRUD操作**：提供创建、删除、修改、查询列表和详情功能
3. **状态管理**：支持未付款、已付款、已取消、已完成等多种状态
4. **逻辑删除**：采用逻辑删除方式，保留数据完整性
5. **统一响应格式**：使用AjaxResult统一响应格式，包含code、msg、data字段
6. **参数校验**：在Controller层进行必要的参数校验，确保数据合法性

## 使用建议

1. 前端调用时，建议使用统一的API请求工具，如axios
2. 创建订单时，需确保传入所有必填参数
3. 查询列表时，可以根据需求添加筛选条件
4. 对于频繁访问的接口，可以考虑添加缓存机制
5. 订单状态变更时，需注意状态流转的合理性

## 裁判列表接口返回示例

**接口地址**：`/referee/list`

**请求方法**：`GET`

**响应示例**：
```json
{
  "code": 200,
  "msg": "成功",
  "data": [
    {
      "id": 1,
      "name": "张裁判",
      "avatarPath": "http://example.com/avatar1.jpg",
      "level": "国家级",
      "price": "1000",
      "orderCount": 5,
      "scheduleLog": "[{
        \"date\": \"2026-01-28\",
        \"time\": [
          {\"period\": \"12:00-14:00\", \"status\": 0},
          {\"period\": \"14:00-16:00\", \"status\": 0},
          {\"period\": \"16:00-18:00\", \"status\": 1},
          {\"period\": \"18:00-20:00\", \"status\": 1},
          {\"period\": \"20:00-22:00\", \"status\": 1},
          {\"period\": \"22:00-24:00\", \"status\": 0}
        ]
      }, {
        \"date\": \"2026-01-29\",
        \"time\": [
          {\"period\": \"12:00-14:00\", \"status\": 0},
          {\"period\": \"14:00-16:00\", \"status\": 0},
          {\"period\": \"16:00-18:00\", \"status\": 1},
          {\"period\": \"18:00-20:00\", \"status\": 1},
          {\"period\": \"20:00-22:00\", \"status\": 1},
          {\"period\": \"22:00-24:00\", \"status\": 0}
        ]
      }]"
    },
    {
      "id": 2,
      "name": "李裁判",
      "avatarPath": "http://example.com/avatar2.jpg",
      "level": "省级",
      "price": "800",
      "orderCount": 3,
      "scheduleLog": "[{
        \"date\": \"2026-01-28\",
        \"time\": [
          {\"period\": \"12:00-14:00\", \"status\": 0},
          {\"period\": \"14:00-16:00\", \"status\": 0},
          {\"period\": \"16:00-18:00\", \"status\": 0},
          {\"period\": \"18:00-20:00\", \"status\": 0},
          {\"period\": \"20:00-22:00\", \"status\": 0},
          {\"period\": \"22:00-24:00\", \"status\": 0}
        ]
      }, {
        \"date\": \"2026-01-29\",
        \"time\": [
          {\"period\": \"12:00-14:00\", \"status\": 0},
          {\"period\": \"14:00-16:00\", \"status\": 0},
          {\"period\": \"16:00-18:00\", \"status\": 0},
          {\"period\": \"18:00-20:00\", \"status\": 0},
          {\"period\": \"20:00-22:00\", \"status\": 0},
          {\"period\": \"22:00-24:00\", \"status\": 0}
        ]
      }]"
    }
  ]
}
```

**说明**：
- `scheduleLog`字段返回的是JSON字符串，包含今天和明天的时间段信息
- 每个时间段包含`period`（时间段）和`status`（状态）字段
- `status`为0表示该时间段可用，为1表示该时间段已被占用
