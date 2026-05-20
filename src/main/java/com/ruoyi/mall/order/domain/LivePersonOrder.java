package com.ruoyi.mall.order.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 直播人员订单实体类
 * 描述：用于存储直播人员订单信息，包括订单基本信息、直播人员信息、比赛信息等
 * 
 * @author ruoyi
 * @version 1.0.0
 * @since 2026-04-27
 */
@Data
@TableName("mall_live_person_order")
public class LivePersonOrder {

    /**
     * 主键ID，自增
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 订单号（格式：LP + 时间戳）
     */
    private String orderNo;

    /**
     * 直播人员ID
     */
    private Long livePersonId;

    /**
     * 直播人员姓名
     */
    private String livePersonName;

    /**
     * 直播人员资质描述
     */
    private String qualification;

    /**
     * 联系人姓名
     */
    private String contactName;

    /**
     * 联系人电话
     */
    private String contactPhone;

    /**
     * 比赛信息（如：主队 vs 客队）
     */
    private String matchInfo;

    /**
     * 比赛时间
     */
    private String matchTime;

    /**
     * 比赛地点
     */
    private String matchLocation;

    /**
     * 订单总金额（单位：元）
     */
    private String totalAmount;

    /**
     * 备注
     */
    private String remark;

    /**
     * 订单状态：0-未付款 1-已付款 2-已取消 3-已完成
     */
    private Integer status;

    /**
     * 微信支付官方订单号（transaction_id）
     */
    @JsonIgnore
    private String transactionId;

    /**
     * 支付时间
     */
    private LocalDateTime payTime;

    /**
     * 逻辑删除：0-未删 1-已删
     */
    @TableLogic
    private Integer deleted;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
