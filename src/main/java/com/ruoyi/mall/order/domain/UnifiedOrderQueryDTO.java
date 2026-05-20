package com.ruoyi.mall.order.domain;

import lombok.Data;

/**
 * 统一订单查询DTO
 * 描述：用于查询统一订单列表的请求参数
 * 
 * @author ruoyi
 * @version 1.0.0
 * @since 2026-05-12
 */
@Data
public class UnifiedOrderQueryDTO {

    /**
     * 订单类型：REF-裁判订单, LIVE-直播订单, MALL-商城订单（为空则查询全部）
     */
    private String orderType;

    /**
     * 订单状态：0-未付款 1-已付款 2-已取消 3-已完成（为空则查询全部）
     */
    private Integer status;

    /**
     * 订单号（模糊查询）
     */
    private String orderNo;

    /**
     * 联系人姓名（模糊查询）
     */
    private String contactName;

    /**
     * 页码
     */
    private Integer pageNum = 1;

    /**
     * 每页数量
     */
    private Integer pageSize = 10;
}