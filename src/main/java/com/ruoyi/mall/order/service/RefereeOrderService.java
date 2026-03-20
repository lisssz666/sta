package com.ruoyi.mall.order.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ruoyi.mall.order.domain.RefereeOrder;
import com.ruoyi.mall.order.domain.vo.RefereeOrderVO;

import java.util.List;

public interface RefereeOrderService {

    /** 创建裁判订单 */
    RefereeOrder createRefereeOrder(RefereeOrder refereeOrder);
    
    /** 创建裁判订单（带参数） */
    RefereeOrder createRefereeOrder(Long refereeId, String contactName, String contactPhone, String totalAmount, 
                                   Long gameId, String matchTime, String remark, String matchInfo, String matchLocation, String scheduleLog);
    /** 根据ID删除订单 */
    boolean removeOrderById(Long id);
    /** 根据ID修改订单 */
    boolean updateOrderById(RefereeOrder order);
    /** 根据ID查询订单 */
    RefereeOrder getOrderById(Long id);
    /** 根据ID查询订单VO */
    RefereeOrderVO getOrderVOById(Long id);
    /** 根据参数查询订单列表 */
    List<RefereeOrderVO> listOrdersNoPage(Long refereeId, String contactPhone, Integer status);
    
    /** 取消订单（包含退款流程和时间段放开） */
    boolean cancelOrder(Long orderId) throws Exception;
}
