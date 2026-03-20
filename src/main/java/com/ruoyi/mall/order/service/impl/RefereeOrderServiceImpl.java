package com.ruoyi.mall.order.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.mall.order.domain.RefereeOrder;
import com.ruoyi.mall.order.domain.vo.RefereeOrderVO;
import com.ruoyi.mall.order.mapper.RefereeOrderMapper;
import com.ruoyi.mall.order.service.RefereeOrderService;
import com.ruoyi.project.referee.domain.StaRefereeInfoEntity;
import com.ruoyi.project.referee.service.IStaRefereeInfoService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ruoyi.mall.order.utils.ScheduleLogUtils;

// 裁判订单实现
@Service
public class RefereeOrderServiceImpl extends ServiceImpl<RefereeOrderMapper, RefereeOrder>
        implements RefereeOrderService {

    @Autowired
    private IStaRefereeInfoService refereeInfoService;
    
    @Autowired
    private com.ruoyi.project.game.service.IStaGameService staGameService;
    //文件服务器地址
    @Value("${spring.upload.server}")
    private String uploadServer;

    @Override
    public RefereeOrder createRefereeOrder(RefereeOrder refereeOrder) {
        // 1. 生成订单号
        String orderNo = "RF" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        refereeOrder.setOrderNo(orderNo);
        // 2. 设置默认状态为待支付
        if (refereeOrder.getStatus() == null) {
            refereeOrder.setStatus(0);
        }
        // 3. 保存订单
        this.save(refereeOrder);
        // 4. 返回订单
        return refereeOrder;  
    }

    @Override
    public RefereeOrder createRefereeOrder(Long refereeId, String contactName, String contactPhone, String totalAmount, 
                                          Long gameId, String matchTime, String remark, String matchInfo, String matchLocation, String scheduleLog) {
        // 1. 获取裁判信息
        StaRefereeInfoEntity refereeInfo = refereeInfoService.getById(refereeId);
        if (refereeInfo == null) {
            throw new IllegalArgumentException("裁判信息不存在");
        }

        // 2. 创建裁判订单对象
        RefereeOrder refereeOrder = new RefereeOrder();
        refereeOrder.setRefereeId(refereeId);
        refereeOrder.setContactName(contactName);
        refereeOrder.setContactPhone(contactPhone);
        refereeOrder.setTotalAmount(totalAmount);
        refereeOrder.setMatchTime(matchTime);
        refereeOrder.setRemark(remark);

        // 3. 如果提供了scheduleLog，将其存到matchTime字段
        if (!org.springframework.util.StringUtils.isEmpty(scheduleLog)) {
            refereeOrder.setMatchTime(scheduleLog);
        }

        // 4. 如果提供了比赛ID，查询比赛信息
        if (gameId != null) {
            try {
                // 使用注入的StaGameService查询比赛信息
                com.ruoyi.project.game.domain.StaGame game = staGameService.selectStaGameById(gameId);
                if (game != null) {
                    // 设置比赛信息
                    refereeOrder.setMatchInfo(game.getHomeName() + " vs " + game.getAwayName());
                    // 如果没有提供scheduleLog，使用比赛时间
                    if (org.springframework.util.StringUtils.isEmpty(scheduleLog)) {
                        refereeOrder.setMatchTime(game.getPlayingTime());
                    }
                    refereeOrder.setMatchLocation(game.getVenueName() + " " + game.getGameAddr());
                }
            } catch (Exception e) {
                // 忽略异常，继续使用传入的参数
            }
        }

        // 5. 如果没有比赛信息，使用传入的参数
        if (org.springframework.util.StringUtils.isEmpty(refereeOrder.getMatchInfo())) {
            refereeOrder.setMatchInfo(matchInfo);
        }
        if (org.springframework.util.StringUtils.isEmpty(refereeOrder.getMatchLocation())) {
            refereeOrder.setMatchLocation(matchLocation);
        }

        // 6. 填充裁判信息
        refereeOrder.setRefereeName(refereeInfo.getName());
        refereeOrder.setRefereeLevel(refereeInfo.getLevel());

        // 7. 创建订单
        RefereeOrder result = createRefereeOrder(refereeOrder);

        // 8. 如果提供了scheduleLog，更新裁判的服务时间段
        if (!org.springframework.util.StringUtils.isEmpty(scheduleLog)) {
            updateRefereeScheduleLog(refereeInfo, scheduleLog);
        }

        return result;
    }

    /**
     * 更新裁判的服务时间段
     * @param refereeInfo 裁判信息
     * @param scheduleLog 时间段日志
     */
    private void updateRefereeScheduleLog(StaRefereeInfoEntity refereeInfo, String scheduleLog) {
        // 1. 初始化时间段映射，用于存储日期到时间段集合的映射
        Map<String, Set<String>> timeSlotsMap = new HashMap<>();
        
        // 2. 解析现有的scheduleLog（如果存在）
        ScheduleLogUtils.parseExistingScheduleLog(refereeInfo, timeSlotsMap);
        
        // 3. 解析并添加新的scheduleLog
        ScheduleLogUtils.parseScheduleLog(scheduleLog, timeSlotsMap);
        
        // 4. 过滤过期时间段并生成新的scheduleLog
        String updatedScheduleLog = ScheduleLogUtils.generateUpdatedScheduleLog(timeSlotsMap);
        
        // 5. 更新裁判信息
        refereeInfo.setScheduleLog(updatedScheduleLog);
        try {
            refereeInfoService.updateById(refereeInfo);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    


    @Override
    public boolean removeOrderById(Long id) {
        return this.removeById(id);
    }

    @Override
    public boolean updateOrderById(RefereeOrder order) {
        return this.updateById(order);
    }

    @Override
    public RefereeOrder getOrderById(Long id) {
        return this.getById(id);
    }

    @Override
    public RefereeOrderVO getOrderVOById(Long id) {
        // 1. 查询订单
        RefereeOrder order = this.getById(id);
        if (order == null) {
            return null;
        }
        
        // 2. 转换为VO并添加裁判头像
        return convertToVO(order);
    }

    @Override
    public List<RefereeOrderVO> listOrdersNoPage(Long refereeId, String contactPhone, Integer status) {
        // 1. 构建查询条件
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<RefereeOrder> queryWrapper = new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        if (refereeId != null) {
            queryWrapper.eq("referee_id", refereeId);
        }
        if (contactPhone != null) {
            queryWrapper.eq("contact_phone", contactPhone);
        }
        if (status != null) {
            queryWrapper.eq("status", status);
        }
        // 添加按创建时间降序排序，确保最新订单排在前面
        queryWrapper.orderByDesc("create_time");
        // 2. 查询订单列表
        List<RefereeOrder> orderList = this.list(queryWrapper);
        // 3. 转换为VO列表
        return orderList.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    // 转换为VO
    private RefereeOrderVO convertToVO(RefereeOrder order) {
        RefereeOrderVO vo = new RefereeOrderVO();
        BeanUtils.copyProperties(order, vo);
        
        // 添加裁判头像路径
        if (order.getRefereeId() != null) {
            StaRefereeInfoEntity refereeInfo = refereeInfoService.getById(order.getRefereeId());
            if (refereeInfo != null) {
                // 检查头像路径是否已经包含服务器地址
                vo.setAvatarPath(refereeInfo.getAvatarPath());
            }
        }
        
        return vo;
    }
    
    @Override
    public boolean cancelOrder(Long orderId) throws Exception {
        // 1. 获取订单信息
        RefereeOrder order = this.getById(orderId);
        if (order == null) {
            throw new IllegalArgumentException("订单不存在");
        }
        
        // 2. 检查订单状态，只有未付款或已付款的订单可以取消
        if (order.getStatus() == 2 || order.getStatus() == 3) {
            throw new IllegalArgumentException("订单状态不允许取消");
        }
        
        // 3. 执行退款流程（如果订单已付款）
        if (order.getStatus() == 1) {
            // 这里实现退款逻辑，实际项目中需要调用支付平台的退款API
            // 例如微信支付的退款接口
            // 暂时模拟退款成功
            System.out.println("执行退款流程，订单号：" + order.getOrderNo());
            // TODO: 集成实际的退款API
        }
        
        // 4. 放开已选择的时间段
        if (order.getRefereeId() != null && !org.springframework.util.StringUtils.isEmpty(order.getMatchTime())) {
            // 获取裁判信息
            StaRefereeInfoEntity refereeInfo = refereeInfoService.getById(order.getRefereeId());
            if (refereeInfo != null) {
                // 从裁判的scheduleLog中移除对应的时间段
                releaseScheduleSlots(refereeInfo, order.getMatchTime());
            }
        }
        
        // 5. 更新订单状态为已取消
        order.setStatus(2);
        order.setUpdateTime(LocalDateTime.now());
        boolean result = this.updateById(order);
        
        return result;
    }
    
    /**
     * 释放裁判的时间段（从scheduleLog中移除对应的时间段）
     * @param refereeInfo 裁判信息
     * @param scheduleLog 时间段日志
     */
    private void releaseScheduleSlots(StaRefereeInfoEntity refereeInfo, String scheduleLog) {
        // 使用ScheduleLogUtils中的公共方法来释放时间段
        String updatedScheduleLog = ScheduleLogUtils.releaseScheduleSlots(refereeInfo, scheduleLog);
        
        // 更新裁判信息
        refereeInfo.setScheduleLog(updatedScheduleLog);
        try {
            refereeInfoService.updateById(refereeInfo);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
