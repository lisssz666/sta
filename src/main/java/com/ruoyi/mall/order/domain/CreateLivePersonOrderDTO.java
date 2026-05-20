package com.ruoyi.mall.order.domain;

import lombok.Data;

/**
 * 创建直播人员订单DTO
 * 描述：用于接收前端创建直播人员订单的请求参数
 * 
 * @author ruoyi
 * @version 1.0.0
 * @since 2026-04-27
 */
@Data
public class CreateLivePersonOrderDTO {

    /**
     * 直播人员ID（必填）
     */
    private Long livePersonId;

    /**
     * 联系人姓名（必填）
     */
    private String contactName;

    /**
     * 联系人电话（必填）
     */
    private String contactPhone;

    /**
     * 订单总金额（必填，单位：元）
     */
    private String totalAmount;

    /**
     * 比赛ID（可选，如果提供则自动填充比赛信息）
     */
    private Long gameId;

    /**
     * 比赛时间（可选）
     */
    private String matchTime;

    /**
     * 备注（可选）
     */
    private String remark;

    /**
     * 比赛信息（可选，如：主队 vs 客队）
     */
    private String matchInfo;

    /**
     * 比赛地点（可选）
     */
    private String matchLocation;
}
