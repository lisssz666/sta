package com.ruoyi.mall.order.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 统一订单VO
 * 描述：整合裁判订单、直播订单、商城订单等，通过orderType字段区分订单类型
 * 
 * @author ruoyi
 * @version 1.0.0
 * @since 2026-05-12
 */
@Data
public class UnifiedOrderVO {

    /**
     * 订单ID
     */
    private Long id;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 订单类型：REF-裁判订单, LIVE-直播订单, MALL-商城订单
     */
    private String orderType;

    /**
     * 订单类型名称
     */
    private String orderTypeName;

    /**
     * 关联人员/商品名称（裁判姓名、直播人员姓名、商户名称等）
     */
    private String relatedName;

    /**
     * 关联人员/商品描述（裁判等级、直播资质等）
     */
    private String relatedDesc;

    /**
     * 联系人姓名
     */
    private String contactName;

    /**
     * 联系人电话
     */
    private String contactPhone;

    /**
     * 比赛/订单信息
     */
    private String matchInfo;

    /**
     * 比赛时间
     */
    private String matchTime;

    /**
     * 比赛/收货地点
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
     * 订单状态名称
     */
    private String statusName;

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
     * 头像/商品图片路径
     */
    private String avatarUrl;

    /**
     * 将订单状态转换为中文名称
     */
    public void setStatusName() {
        if (this.status == null) {
            this.statusName = "未知";
            return;
        }
        switch (this.status) {
            case 0:
                this.statusName = "未付款";
                break;
            case 1:
                this.statusName = "已付款";
                break;
            case 2:
                this.statusName = "已取消";
                break;
            case 3:
                this.statusName = "已完成";
                break;
            default:
                this.statusName = "未知";
        }
    }
}