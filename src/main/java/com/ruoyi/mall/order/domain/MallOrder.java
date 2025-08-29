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
    //0未付款 1已付款 2商家已接单 3正在配送 4已完成
    private Integer status;
    /** 微信支付官方订单号（transaction_id） */
    private String transactionId;
    private LocalDateTime payTime;
    @TableLogic
    private Integer deleted;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    // 一对多：订单明细
    @TableField(exist = false)
    private List<MallOrderItem> items;
}