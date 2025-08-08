package com.ruoyi.mall.order.domain.vo;

import lombok.Data;

@Data
public class OrderItemVO {
    private Long productId;
    private String productName;
    private Integer quantity;
    private String unitPrice;
    private String totalPrice;
    private String coverImg;   // 绝对路径
}