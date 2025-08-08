package com.ruoyi.mall.order.domain.vo;

import lombok.Data;

import java.util.List;

@Data
public class OrderListVO {
    private Long id;
    private String orderNo;
    private String totalAmount;
    private String address;
    private String phone;
    private String remark;
    private String status;
    private String createTime;
    private List<OrderItemVO> items;
}
