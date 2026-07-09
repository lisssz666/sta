package com.ruoyi.mall.order.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.mall.order.domain.LivePersonOrder;
import com.ruoyi.mall.order.domain.vo.LivePersonOrderVO;
import com.ruoyi.mall.order.mapper.LivePersonOrderMapper;
import com.ruoyi.mall.order.service.LivePersonOrderService;
import com.ruoyi.project.live.domain.LivePerson;
import com.ruoyi.project.live.service.LivePersonService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 直播人员订单Service实现类
 * 描述：实现直播人员订单的业务操作方法
 * 
 * @author ruoyi
 * @version 1.0.0
 * @since 2026-04-27
 */
@Service
public class LivePersonOrderServiceImpl extends ServiceImpl<LivePersonOrderMapper, LivePersonOrder>
        implements LivePersonOrderService {

    @Autowired
    private LivePersonService livePersonService;

    @Autowired
    private com.ruoyi.project.game.service.IStaGameService staGameService;

    @Override
    public LivePersonOrder createLivePersonOrder(LivePersonOrder livePersonOrder) {
        // 1. 生成订单号（格式：LP + 时间戳）
        String orderNo = "LP" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        livePersonOrder.setOrderNo(orderNo);

        // 2. 设置默认状态为待支付
        if (livePersonOrder.getStatus() == null) {
            livePersonOrder.setStatus(0);
        }

        // 3. 保存订单
        this.save(livePersonOrder);

        // 4. 返回订单
        return livePersonOrder;
    }

    @Override
    public LivePersonOrder createLivePersonOrder(Long livePersonId, String contactName, String contactPhone,
                                                  String totalAmount, Long gameId, String matchTime, String remark,
                                                  String matchInfo, String matchLocation) {
        // 1. 获取直播人员信息
        LivePerson livePerson = livePersonService.getById(livePersonId);
        if (livePerson == null) {
            throw new IllegalArgumentException("直播人员信息不存在");
        }

        // 2. 创建直播人员订单对象
        LivePersonOrder livePersonOrder = new LivePersonOrder();
        livePersonOrder.setLivePersonId(livePersonId);
        livePersonOrder.setContactName(contactName);
        livePersonOrder.setContactPhone(contactPhone);
        livePersonOrder.setTotalAmount(totalAmount);
        livePersonOrder.setMatchTime(matchTime);
        livePersonOrder.setRemark(remark);

        // 3. 如果提供了比赛ID，查询比赛信息
        if (gameId != null) {
            try {
                // 使用注入的StaGameService查询比赛信息
                com.ruoyi.project.game.domain.StaGame game = staGameService.selectStaGameById(gameId);
                if (game != null) {
                    // 设置比赛信息
                    livePersonOrder.setMatchInfo(game.getHomeName() + " vs " + game.getAwayName());
                    // 如果没有提供比赛时间，使用比赛时间
                    if (org.springframework.util.StringUtils.isEmpty(matchTime)) {
                        livePersonOrder.setMatchTime(game.getPlayingTime());
                    }
                    livePersonOrder.setMatchLocation(game.getVenueName() + " " + game.getGameAddr());
                }
            } catch (Exception e) {
                // 忽略异常，继续使用传入的参数
            }
        }

        // 4. 如果没有比赛信息，使用传入的参数
        if (org.springframework.util.StringUtils.isEmpty(livePersonOrder.getMatchInfo())) {
            livePersonOrder.setMatchInfo(matchInfo);
        }
        if (org.springframework.util.StringUtils.isEmpty(livePersonOrder.getMatchLocation())) {
            livePersonOrder.setMatchLocation(matchLocation);
        }

        // 5. 填充直播人员信息
        livePersonOrder.setLivePersonName(livePerson.getName());
        livePersonOrder.setQualification(livePerson.getQualification());

        // 6. 创建订单
        LivePersonOrder result = createLivePersonOrder(livePersonOrder);

        return result;
    }

    @Override
    public boolean removeOrderById(Long id) {
        return this.removeById(id);
    }

    @Override
    public boolean updateOrderById(LivePersonOrder order) {
        return this.updateById(order);
    }

    @Override
    public LivePersonOrder getOrderById(Long id) {
        return this.getById(id);
    }

    @Override
    public LivePersonOrderVO getOrderVOById(Long id) {
        // 1. 查询订单
        LivePersonOrder order = this.getById(id);
        if (order == null) {
            return null;
        }

        // 2. 转换为VO并添加直播人员头像
        return convertToVO(order);
    }

    @Override
    public List<LivePersonOrderVO> listOrdersNoPage(Long livePersonId, String contactPhone, Integer status) {
        // 1. 构建查询条件
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<LivePersonOrder> queryWrapper = 
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        
        if (livePersonId != null) {
            queryWrapper.eq("live_person_id", livePersonId);
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
        List<LivePersonOrder> orderList = this.list(queryWrapper);
        
        // 3. 转换为VO列表
        return orderList.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public List<LivePersonOrderVO> listOrdersByUserId(Long userId, Long livePersonId, String contactPhone, Integer status) {
        // 1. 构建查询条件，添加用户ID过滤
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<LivePersonOrder> queryWrapper = 
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        
        if (livePersonId != null) {
            queryWrapper.eq("live_person_id", livePersonId);
        }
        if (contactPhone != null) {
            queryWrapper.eq("contact_phone", contactPhone);
        }
        if (status != null) {
            queryWrapper.eq("status", status);
        }
        
        queryWrapper.orderByDesc("create_time");
        
        // 2. 查询订单列表
        List<LivePersonOrder> orderList = this.list(queryWrapper);
        
        // 3. 转换为VO列表
        return orderList.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public LivePersonOrderVO getOrderVOByUserId(Long userId, Long orderId) {
        // 1. 查询订单（带用户ID过滤）
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<LivePersonOrder> queryWrapper = 
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        queryWrapper.eq("id", orderId);
        queryWrapper.eq("user_id", userId);
        LivePersonOrder order = this.getOne(queryWrapper);
        if (order == null) {
            return null;
        }
        // 2. 转换为VO并添加直播人员头像
        return convertToVO(order);
    }

    @Override
    public boolean cancelOrderByUserId(Long userId, Long orderId) throws Exception {
        // 1. 查询订单（带用户ID过滤）
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<LivePersonOrder> queryWrapper = 
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        queryWrapper.eq("id", orderId);
        queryWrapper.eq("user_id", userId);
        LivePersonOrder order = this.getOne(queryWrapper);
        
        if (order == null) {
            throw new IllegalArgumentException("订单不存在或无权限访问");
        }
        
        // 2. 调用原有的取消逻辑
        return cancelOrder(orderId);
    }

    @Override
    public boolean removeOrderByUserId(Long userId, Long orderId) {
        // 删除订单（带用户ID过滤）
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<LivePersonOrder> queryWrapper = 
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        queryWrapper.eq("id", orderId);
        queryWrapper.eq("user_id", userId);
        return this.remove(queryWrapper);
    }

    @Override
    public List<LivePersonOrderVO> listAll() {
        // 查询所有订单，按创建时间降序排序
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<LivePersonOrder> queryWrapper = 
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        queryWrapper.orderByDesc("create_time");
        List<LivePersonOrder> orderList = this.list(queryWrapper);
        return orderList.parallelStream().map(this::convertToVO).collect(Collectors.toList());
    }

    /**
     * 转换为VO
     * 描述：将订单实体转换为VO，并添加直播人员头像等扩展信息
     *
     * @param order 订单实体
     * @return 订单VO
     */
    private LivePersonOrderVO convertToVO(LivePersonOrder order) {
        LivePersonOrderVO vo = new LivePersonOrderVO();
        BeanUtils.copyProperties(order, vo);

        // 添加直播人员头像路径
        if (order.getLivePersonId() != null) {
            LivePerson livePerson = livePersonService.getById(order.getLivePersonId());
            if (livePerson != null) {
                vo.setAvatarUrl(livePerson.getAvatarUrl());
            }
        }

        return vo;
    }

    @Override
    public boolean cancelOrder(Long orderId) throws Exception {
        // 1. 获取订单信息
        LivePersonOrder order = this.getById(orderId);
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

        // 4. 更新订单状态为已取消
        order.setStatus(2);
        order.setUpdateTime(LocalDateTime.now());
        boolean result = this.updateById(order);

        return result;
    }
}
