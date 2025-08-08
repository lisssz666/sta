package com.ruoyi.mall.order.domain;


import com.ruoyi.framework.web.domain.BaseEntity;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 订单明细 DTO，对应一条商品
 */
@Data
public class OrderItemDTO extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 商品 ID */
//    @NotNull(message="商品ID不能为空")
    private Long productId;

    /** 购买数量 */
//    @Min(value = 1, message="数量必须≥1")
    private Integer quantity;
}