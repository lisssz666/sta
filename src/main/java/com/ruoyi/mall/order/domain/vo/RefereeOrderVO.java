package com.ruoyi.mall.order.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RefereeOrderVO {
    private Long id;
    private String orderNo;
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
    private Integer status;
    private String transactionId;
    private LocalDateTime payTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private String avatarPath; // 裁判头像路径
}
