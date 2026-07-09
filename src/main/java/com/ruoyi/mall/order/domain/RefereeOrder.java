package com.ruoyi.mall.order.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.LocalDateTime;

// 裁判订单主表
@Data
@TableName("mall_referee_order")
public class RefereeOrder {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String orderNo;
    /** 用户ID */
    private Long userId;
    private Long refereeId;
    private String refereeName;
    private String refereeLevel;
    private String contactName;
    private String contactPhone;
    private String matchInfo;
    private String matchTime;
    private String matchLocation;
    private String totalAmount;
    private String remark;
    //0未付款 1已付款 2已取消 3已完成
    private Integer status;
    /** 微信支付官方订单号（transaction_id） */
    @JsonIgnore
    private String transactionId;
    private LocalDateTime payTime;
    @TableLogic
    private Integer deleted;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
