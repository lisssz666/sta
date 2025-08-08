package com.ruoyi.mall.order.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

// 订单主表
@Data
@TableName("mall_order")
public class MallOrder {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long merchantId;
    private String orderNo;
    private Long userId;
    private String totalAmount;
    private String address;
    private String phone;
    private String remark;
    private Integer status;
    @TableLogic
    private Integer deleted;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    // 一对多：订单明细
    @TableField(exist = false)
    private List<MallOrderItem> items;
}