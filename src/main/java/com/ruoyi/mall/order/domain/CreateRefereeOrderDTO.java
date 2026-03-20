package com.ruoyi.mall.order.domain;

import lombok.Data;

/**
 * 创建裁判订单DTO
 */
@Data
public class CreateRefereeOrderDTO {
    private Long refereeId;
    private String contactName;
    private String contactPhone;
    private String totalAmount;
    private Long gameId;
    private String matchTime;
    private String remark;
    private String matchInfo;
    private String matchLocation;
    private String scheduleLog;
}
