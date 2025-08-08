package com.ruoyi.mall.order.domain.vo;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单详情 / 订单列表统一返回对象
 */
@Data
public class OrderDetailVO {

    /** 订单主键 */
    private Long id;

    /** 订单号 */
    private String orderNo;

    /** 订单总额（字符串） */
    private String totalAmount;

    /** 送餐地址 */
    private String address;

    /** 联系电话 */
    private String phone;

    /** 备注 */
    private String remark;

    /** 订单状态：0待支付 1已支付 2已取消 */
    private String status;

    /** 下单时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /** 订单明细列表 */
    private List<OrderItemVO> items;
}
