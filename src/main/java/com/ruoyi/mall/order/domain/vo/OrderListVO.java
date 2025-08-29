package com.ruoyi.mall.order.domain.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderListVO {
    private Long id;
    private String orderNo;
    private String merchantName;
    private String totalAmount;
    private String address;
    private String phone;
    private String remark;
    private Integer status;
    /** 微信支付官方订单号（transaction_id） */
    private String transactionId;
    private LocalDateTime payTime;
    private LocalDateTime createTime;
    private List<OrderItemVO> items;
}
