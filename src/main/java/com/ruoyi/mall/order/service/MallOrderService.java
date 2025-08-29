package com.ruoyi.mall.order.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ruoyi.mall.order.domain.CreateOrderDTO;
import com.ruoyi.mall.order.domain.MallOrder;
import com.ruoyi.mall.order.domain.vo.OrderListVO;


import java.util.List;

public interface MallOrderService {

    /** 根据ID删除订单 */
    boolean removeOrderById(Long id);
    /** 根据ID修改订单 */
    boolean updateOrderById(MallOrder order);
    /** 根据ID查询订单 */
    MallOrder getOrderById(Long id);
    /** 根据参数查询订单列表 */
    List<OrderListVO> listOrdersNoPage(Long merchantId,String phone,String status);
    MallOrder createOrder(CreateOrderDTO dto);
}