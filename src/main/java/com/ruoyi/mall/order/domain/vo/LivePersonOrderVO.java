package com.ruoyi.mall.order.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 直播人员订单VO
 * 描述：用于返回直播人员订单详情信息，包含直播人员头像等扩展信息
 * 
 * @author ruoyi
 * @version 1.0.0
 * @since 2026-04-27
 */
@Data
public class LivePersonOrderVO {

    /**
     * 订单ID
     */
    private Long id;

    /**
     * 订单号
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
     * 比赛信息
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
     * 订单总金额
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
     * 微信支付订单号
     */
    private String transactionId;

    /**
     * 支付时间
     */
    private LocalDateTime payTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 直播人员头像路径
     */
    private String avatarUrl;
}
