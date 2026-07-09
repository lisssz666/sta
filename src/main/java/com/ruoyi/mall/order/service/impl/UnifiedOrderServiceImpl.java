package com.ruoyi.mall.order.service.impl;

import com.ruoyi.framework.web.page.TableDataInfo;
import com.ruoyi.mall.order.domain.UnifiedOrderQueryDTO;
import com.ruoyi.mall.order.domain.vo.LivePersonOrderVO;
import com.ruoyi.mall.order.domain.vo.OrderListVO;
import com.ruoyi.mall.order.domain.vo.RefereeOrderVO;
import com.ruoyi.mall.order.domain.vo.UnifiedOrderVO;
import com.ruoyi.mall.order.service.LivePersonOrderService;
import com.ruoyi.mall.order.service.MallOrderService;
import com.ruoyi.mall.order.service.RefereeOrderService;
import com.ruoyi.mall.order.service.UnifiedOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 统一订单服务实现类
 * 描述：整合裁判订单、直播订单、商城订单的统一查询服务
 * 
 * @author ruoyi
 * @version 1.0.0
 * @since 2026-05-12
 */
@Service
public class UnifiedOrderServiceImpl implements UnifiedOrderService {

    @Autowired
    private RefereeOrderService refereeOrderService;

    @Autowired
    private LivePersonOrderService livePersonOrderService;

    @Autowired
    private MallOrderService mallOrderService;

    @Override
    public TableDataInfo queryUnifiedOrders(UnifiedOrderQueryDTO queryDTO) {
        List<UnifiedOrderVO> allOrders = listUnifiedOrders(queryDTO);
        
        // 分页处理
        int pageNum = queryDTO.getPageNum() != null ? queryDTO.getPageNum() : 1;
        int pageSize = queryDTO.getPageSize() != null ? queryDTO.getPageSize() : 10;
        
        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, allOrders.size());
        
        List<UnifiedOrderVO> pageList = start < allOrders.size() ? allOrders.subList(start, end) : new ArrayList<>();
        
        TableDataInfo result = new TableDataInfo();
        result.setData(pageList);
        result.setTotal(allOrders.size());
        
        return result;
    }

    @Override
    public List<UnifiedOrderVO> listUnifiedOrders(UnifiedOrderQueryDTO queryDTO) {
        List<UnifiedOrderVO> allOrders = new ArrayList<>();
        Long userId = queryDTO.getUserId();

        // 查询裁判订单（带用户隔离）
        if (StringUtils.isEmpty(queryDTO.getOrderType()) || "REF".equals(queryDTO.getOrderType())) {
            List<RefereeOrderVO> refereeOrders = userId != null 
                ? refereeOrderService.listOrdersByUserId(userId, null, null, queryDTO.getStatus())
                : refereeOrderService.listAll();
            allOrders.addAll(refereeOrders.stream().map(this::convertToUnifiedVO).collect(Collectors.toList()));
        }

        // 查询直播订单（带用户隔离）
        if (StringUtils.isEmpty(queryDTO.getOrderType()) || "LIVE".equals(queryDTO.getOrderType())) {
            List<LivePersonOrderVO> liveOrders = userId != null 
                ? livePersonOrderService.listOrdersByUserId(userId, null, null, queryDTO.getStatus())
                : livePersonOrderService.listAll();
            allOrders.addAll(liveOrders.stream().map(this::convertToUnifiedVO).collect(Collectors.toList()));
        }

        // 查询商城订单（带用户隔离）
        if (StringUtils.isEmpty(queryDTO.getOrderType()) || "MALL".equals(queryDTO.getOrderType())) {
            List<OrderListVO> mallOrders = userId != null 
                ? mallOrderService.listOrdersByUserId(userId, null, null, queryDTO.getStatus() != null ? queryDTO.getStatus().toString() : null)
                : mallOrderService.findAll();
            allOrders.addAll(mallOrders.stream().map(this::convertToUnifiedVO).collect(Collectors.toList()));
        }

        // 按状态筛选
        if (queryDTO.getStatus() != null) {
            allOrders = allOrders.stream()
                    .filter(order -> queryDTO.getStatus().equals(order.getStatus()))
                    .collect(Collectors.toList());
        }

        // 按订单号筛选
        if (!StringUtils.isEmpty(queryDTO.getOrderNo())) {
            allOrders = allOrders.stream()
                    .filter(order -> order.getOrderNo().contains(queryDTO.getOrderNo()))
                    .collect(Collectors.toList());
        }

        // 按联系人姓名筛选
        if (!StringUtils.isEmpty(queryDTO.getContactName())) {
            allOrders = allOrders.stream()
                    .filter(order -> !StringUtils.isEmpty(order.getContactName()) && 
                            order.getContactName().contains(queryDTO.getContactName()))
                    .collect(Collectors.toList());
        }

        // 按创建时间降序排序（处理createTime为null的情况）
        allOrders.sort(Comparator.comparing(UnifiedOrderVO::getCreateTime, Comparator.nullsLast(Comparator.naturalOrder())).reversed());

        return allOrders;
    }

    /**
     * 将裁判订单VO转换为统一订单VO
     */
    private UnifiedOrderVO convertToUnifiedVO(RefereeOrderVO source) {
        UnifiedOrderVO vo = new UnifiedOrderVO();
        vo.setId(source.getId());
        vo.setOrderNo(source.getOrderNo());
        vo.setOrderType("REF");
        vo.setOrderTypeName("裁判订单");
        vo.setRelatedName(source.getRefereeName());
        vo.setRelatedDesc(source.getRefereeLevel());
        vo.setContactName(source.getContactName());
        vo.setContactPhone(source.getContactPhone());
        vo.setMatchInfo(source.getMatchInfo());
        vo.setMatchTime(source.getMatchTime());
        vo.setMatchLocation(source.getMatchLocation());
        vo.setTotalAmount(source.getTotalAmount());
        vo.setRemark(source.getRemark());
        vo.setStatus(source.getStatus());
        vo.setStatusName();
        vo.setTransactionId(source.getTransactionId());
        vo.setPayTime(source.getPayTime());
        vo.setCreateTime(source.getCreateTime());
        vo.setUpdateTime(source.getUpdateTime());
        vo.setAvatarUrl(source.getAvatarPath());
        return vo;
    }

    /**
     * 将直播订单VO转换为统一订单VO
     */
    private UnifiedOrderVO convertToUnifiedVO(LivePersonOrderVO source) {
        UnifiedOrderVO vo = new UnifiedOrderVO();
        vo.setId(source.getId());
        vo.setOrderNo(source.getOrderNo());
        vo.setOrderType("LIVE");
        vo.setOrderTypeName("直播订单");
        vo.setRelatedName(source.getLivePersonName());
        vo.setRelatedDesc(source.getQualification());
        vo.setContactName(source.getContactName());
        vo.setContactPhone(source.getContactPhone());
        vo.setMatchInfo(source.getMatchInfo());
        vo.setMatchTime(source.getMatchTime());
        vo.setMatchLocation(source.getMatchLocation());
        vo.setTotalAmount(source.getTotalAmount());
        vo.setRemark(source.getRemark());
        vo.setStatus(source.getStatus());
        vo.setStatusName();
        vo.setTransactionId(source.getTransactionId());
        vo.setPayTime(source.getPayTime());
        vo.setCreateTime(source.getCreateTime());
        vo.setUpdateTime(source.getUpdateTime());
        vo.setAvatarUrl(source.getAvatarUrl());
        return vo;
    }

    /**
     * 将商城订单VO转换为统一订单VO
     */
    private UnifiedOrderVO convertToUnifiedVO(OrderListVO source) {
        UnifiedOrderVO vo = new UnifiedOrderVO();
        vo.setId(source.getId());
        vo.setOrderNo(source.getOrderNo());
        vo.setOrderType("MALL");
        vo.setOrderTypeName("商城订单");
        vo.setRelatedName(source.getMerchantName());
        vo.setContactName(null);
        vo.setContactPhone(source.getPhone());
        vo.setMatchLocation(source.getAddress());
        vo.setTotalAmount(source.getTotalAmount());
        vo.setRemark(source.getRemark());
        vo.setStatus(source.getStatus());
        vo.setStatusName();
        vo.setTransactionId(source.getTransactionId());
        vo.setPayTime(source.getPayTime());
        vo.setCreateTime(source.getCreateTime());
        return vo;
    }
}