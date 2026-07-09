package com.ruoyi.mall.order.service;

import com.ruoyi.mall.order.domain.LivePersonOrder;
import com.ruoyi.mall.order.domain.vo.LivePersonOrderVO;

import java.util.List;

/**
 * 直播人员订单Service接口
 * 描述：定义直播人员订单的业务操作方法
 * 
 * @author ruoyi
 * @version 1.0.0
 * @since 2026-04-27
 */
public interface LivePersonOrderService {

    /**
     * 创建直播人员订单
     *
     * @param livePersonOrder 直播人员订单信息
     * @return 创建后的订单
     */
    LivePersonOrder createLivePersonOrder(LivePersonOrder livePersonOrder);

    /**
     * 创建直播人员订单（带参数）
     *
     * @param livePersonId 直播人员ID
     * @param contactName 联系人姓名
     * @param contactPhone 联系人电话
     * @param totalAmount 订单金额
     * @param gameId 比赛ID
     * @param matchTime 比赛时间
     * @param remark 备注
     * @param matchInfo 比赛信息
     * @param matchLocation 比赛地点
     * @return 创建后的订单
     */
    LivePersonOrder createLivePersonOrder(Long livePersonId, String contactName, String contactPhone,
                                          String totalAmount, Long gameId, String matchTime, String remark,
                                          String matchInfo, String matchLocation);

    /**
     * 根据ID删除订单（软删除）
     *
     * @param id 订单ID
     * @return 删除是否成功
     */
    boolean removeOrderById(Long id);

    /**
     * 根据ID修改订单
     *
     * @param order 订单信息
     * @return 修改是否成功
     */
    boolean updateOrderById(LivePersonOrder order);

    /**
     * 根据ID查询订单
     *
     * @param id 订单ID
     * @return 订单信息
     */
    LivePersonOrder getOrderById(Long id);

    /**
     * 根据ID查询订单VO（包含直播人员头像等扩展信息）
     *
     * @param id 订单ID
     * @return 订单VO
     */
    LivePersonOrderVO getOrderVOById(Long id);

    /**
     * 根据参数查询订单列表（不分页）
     *
     * @param livePersonId 直播人员ID
     * @param contactPhone 联系人电话
     * @param status 订单状态
     * @return 订单VO列表
     */
    List<LivePersonOrderVO> listOrdersNoPage(Long livePersonId, String contactPhone, Integer status);

    /**
     * 根据用户ID查询订单列表
     *
     * @param userId 用户ID
     * @param livePersonId 直播人员ID
     * @param contactPhone 联系人电话
     * @param status 订单状态
     * @return 订单VO列表
     */
    List<LivePersonOrderVO> listOrdersByUserId(Long userId, Long livePersonId, String contactPhone, Integer status);

    /**
     * 根据用户ID和订单ID查询订单VO
     *
     * @param userId 用户ID
     * @param orderId 订单ID
     * @return 订单VO
     */
    LivePersonOrderVO getOrderVOByUserId(Long userId, Long orderId);

    /**
     * 根据用户ID取消订单
     *
     * @param userId 用户ID
     * @param orderId 订单ID
     * @return 取消是否成功
     * @throws Exception 异常
     */
    boolean cancelOrderByUserId(Long userId, Long orderId) throws Exception;

    /**
     * 根据用户ID删除订单
     *
     * @param userId 用户ID
     * @param orderId 订单ID
     * @return 删除是否成功
     */
    boolean removeOrderByUserId(Long userId, Long orderId);

    /**
     * 查询所有订单
     *
     * @return 订单VO列表
     */
    List<LivePersonOrderVO> listAll();

    /**
     * 取消订单（包含退款流程）
     *
     * @param orderId 订单ID
     * @return 取消是否成功
     * @throws Exception 异常
     */
    boolean cancelOrder(Long orderId) throws Exception;
}
