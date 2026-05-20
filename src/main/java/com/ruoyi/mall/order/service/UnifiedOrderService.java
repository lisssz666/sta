package com.ruoyi.mall.order.service;

import com.ruoyi.framework.web.page.TableDataInfo;
import com.ruoyi.mall.order.domain.UnifiedOrderQueryDTO;
import com.ruoyi.mall.order.domain.vo.UnifiedOrderVO;

import java.util.List;

/**
 * 统一订单服务接口
 * 描述：提供统一的订单查询服务，整合裁判订单、直播订单、商城订单
 * 
 * @author ruoyi
 * @version 1.0.0
 * @since 2026-05-12
 */
public interface UnifiedOrderService {

    /**
     * 查询统一订单列表（支持分页）
     * 
     * @param queryDTO 查询条件
     * @return 分页订单列表
     */
    TableDataInfo queryUnifiedOrders(UnifiedOrderQueryDTO queryDTO);

    /**
     * 查询统一订单列表（不分页）
     * 
     * @param queryDTO 查询条件
     * @return 订单列表
     */
    List<UnifiedOrderVO> listUnifiedOrders(UnifiedOrderQueryDTO queryDTO);
}