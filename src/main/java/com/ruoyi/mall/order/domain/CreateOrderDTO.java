package com.ruoyi.mall.order.domain;

import com.ruoyi.framework.web.domain.BaseEntity;
import lombok.Data;
import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.List;

/**
 * 前端一次下单的完整请求体
 */
@Data
public class CreateOrderDTO extends BaseEntity {

    /** 收货人电话 */
//    @NotBlank(message = "手机号不能为空")
    private String phone;

    /** 收货地址 */
//    @NotBlank(message="地址不能为空")
    private String address;

    /** 备注信息（选填） */
    private String remark;

    /** 商铺id */
    private Long merchantId;

    /** 订单明细集合，至少一条 */
//    @NotEmpty(message="商品不能为空")
//    @Valid
    private List<OrderItemDTO> items;
}
