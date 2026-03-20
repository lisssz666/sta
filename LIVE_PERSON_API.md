# 直播人员管理功能接口清单

## 功能概述

直播人员管理功能提供了对直播人员信息的完整CRUD操作，包括新增、删除、修改、查询列表和详情等功能。接口设计遵循RESTful风格，使用统一的响应格式。

## 接口清单

### 1. 新增直播人员

**接口地址**：`/livePerson/add`

**请求方法**：`POST`

**请求参数**：
```json
{
  "name": "直播人员姓名",
  "avatarFile": "头像文件（MultipartFile类型）",
  "qualification": "直播资质描述",
  "friendPrice": "亲友价（元/队）",
  "liveCount": "直播场次",
  "contentDesc": "直播内容描述",
  "status": "状态：0-禁用 1-启用",
  "isHide": "是否隐藏：0-否 1-是"
}
```

**响应示例**：
```json
{
  "code": 200,
  "msg": "成功",
  "data": 1 // 新增直播人员的ID
}
```

### 2. 删除直播人员

**接口地址**：`/livePerson/delete`

**请求方法**：`DELETE`

**请求参数**：
- `id`：直播人员ID（必填）

**响应示例**：
```json
{
  "code": 200,
  "msg": "成功",
  "data": true // 删除是否成功
}
```

### 3. 修改直播人员信息

**接口地址**：`/livePerson/update`

**请求方法**：`PUT`

**请求参数**：
```json
{
  "id": "直播人员ID",
  "name": "直播人员姓名",
  "avatarFile": "头像文件（MultipartFile类型）",
  "qualification": "直播资质描述",
  "friendPrice": "亲友价（元/队）",
  "liveCount": "直播场次",
  "contentDesc": "直播内容描述",
  "status": "状态：0-禁用 1-启用",
  "isHide": "是否隐藏：0-否 1-是"
}
```

**响应示例**：
```json
{
  "code": 200,
  "msg": "成功",
  "data": true // 修改是否成功
}
```

### 4. 查询直播人员列表

**接口地址**：`/livePerson/list`

**请求方法**：`GET`

**请求参数**：无

**响应示例**：
```json
{
  "code": 200,
  "msg": "成功",
  "data": [
    {
      "id": 1,
      "name": "黄总",
      "avatarUrl": "/images/avatar1.jpg",
      "qualification": "资深直播数据员",
      "friendPrice": 150,
      "liveCount": 182,
      "contentDesc": "全场直播/统计全队数据/个人集锦等",
      "status": 1,
      "isHide": 0,
      "createTime": "2023-10-01 10:00:00",
      "updateTime": "2023-10-01 10:00:00"
    },
    {
      "id": 2,
      "name": "张导",
      "avatarUrl": "/images/avatar2.jpg",
      "qualification": "专业篮球解说",
      "friendPrice": 200,
      "liveCount": 234,
      "contentDesc": "专业解说/战术分析/精彩回放",
      "status": 1,
      "isHide": 0,
      "createTime": "2023-10-02 09:30:00",
      "updateTime": "2023-10-02 09:30:00"
    }
  ]
}
```

### 5. 查询启用状态的直播人员列表

**接口地址**：`/livePerson/listActive`

**请求方法**：`GET`

**请求参数**：无

**响应示例**：
```json
{
  "code": 200,
  "msg": "成功",
  "data": [
    {
      "id": 1,
      "name": "黄总",
      "avatarUrl": "/images/avatar1.jpg",
      "qualification": "资深直播数据员",
      "friendPrice": 150,
      "liveCount": 182,
      "contentDesc": "全场直播/统计全队数据/个人集锦等",
      "status": 1,
      "isHide": 0,
      "createTime": "2023-10-01 10:00:00",
      "updateTime": "2023-10-01 10:00:00"
    }
  ]
}
```

### 6. 查询直播人员详情

**接口地址**：`/livePerson/getLivePersonById`

**请求方法**：`GET`

**请求参数**：
- `id`：直播人员ID（必填）

**响应示例**：
```json
{
  "code": 200,
  "msg": "成功",
  "data": {
    "id": 1,
    "name": "黄总",
    "avatarUrl": "/images/avatar1.jpg",
    "qualification": "资深直播数据员",
    "friendPrice": 150,
    "liveCount": 182,
    "contentDesc": "全场直播/统计全队数据/个人集锦等",
    "status": 1,
    "isHide": 0,
    "createTime": "2023-10-01 10:00:00",
    "updateTime": "2023-10-01 10:00:00"
  }
}
```

## 数据库设计

### 直播人员信息表（sta_live_person）

```sql
CREATE TABLE `sta_live_person` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(255) DEFAULT NULL COMMENT '直播人员姓名',
  `avatar_url` varchar(500) DEFAULT NULL COMMENT '直播人员头像URL',
  `qualification` varchar(500) DEFAULT NULL COMMENT '直播资质描述',
  `friend_price` decimal(10,2) DEFAULT NULL COMMENT '亲友价（元/队）',
  `live_count` int(11) DEFAULT '0' COMMENT '直播场次总数',
  `content_desc` varchar(1000) DEFAULT NULL COMMENT '直播内容描述',
  `status` int(11) DEFAULT '1' COMMENT '状态：0-禁用 1-启用',
  `is_hide` int(11) DEFAULT '0' COMMENT '是否隐藏：1-是 0-否',
  `deleted` int(11) DEFAULT '0' COMMENT '逻辑删除：0-未删 1-已删',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='直播人员信息表';
```

## 项目结构

```
src/main/java/com/ruoyi/mall/live/
├── controller/
│   └── LivePersonController.java       # 直播人员控制器
├── domain/
│   └── MallLivePerson.java             # 直播人员实体类
├── mapper/
│   └── MallLivePersonMapper.java       # 直播人员Mapper接口
└── service/
    ├── MallLivePersonService.java      # 直播人员Service接口
    └── impl/
        └── MallLivePersonServiceImpl.java # 直播人员Service实现类
```

## 功能特点

1. **高效简洁的接口设计**：遵循RESTful风格，接口命名规范，参数清晰
2. **完整的CRUD操作**：提供新增、删除、修改、查询列表和详情功能
3. **状态管理**：支持启用/禁用和隐藏/显示功能
4. **逻辑删除**：采用逻辑删除方式，保留数据完整性
5. **默认值设置**：在Service层设置合理的默认值，简化客户端调用
6. **统一响应格式**：使用AjaxResult统一响应格式，包含code、msg、data字段
7. **文件上传支持**：支持头像文件上传功能

## 使用建议

1. 前端调用时，建议使用统一的API请求工具，如axios
2. 文件上传时，需使用FormData格式
3. 查询列表时，可以根据需求添加分页功能
4. 对于频繁访问的接口，可以考虑添加缓存机制
