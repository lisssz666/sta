package com.ruoyi.mall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.mall.order.domain.MallOrderItem;

import java.util.List;

public interface MallOrderItemService extends IService<MallOrderItem> {
    List<MallOrderItem> listItemsByOrderId(Long id);
}