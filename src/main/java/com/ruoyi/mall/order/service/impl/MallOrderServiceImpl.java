package com.ruoyi.mall.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.ruoyi.mall.merchant.domain.MallMerchant;
import com.ruoyi.mall.merchant.service.MallMerchantService;
import com.ruoyi.mall.order.domain.CreateOrderDTO;
import com.ruoyi.mall.order.domain.MallOrder;
import com.ruoyi.mall.order.domain.MallOrderItem;
import com.ruoyi.mall.order.domain.OrderItemDTO;
import com.ruoyi.mall.order.domain.vo.OrderItemVO;
import com.ruoyi.mall.order.domain.vo.OrderListVO;
import com.ruoyi.mall.order.mapper.MallOrderMapper;
import com.ruoyi.mall.order.service.MallOrderItemService;
import com.ruoyi.mall.order.service.MallOrderService;
import com.ruoyi.mall.product.domain.MallProduct;
import com.ruoyi.mall.product.service.MallProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

// 订单实现
@Service
@RequiredArgsConstructor
public class MallOrderServiceImpl extends ServiceImpl<MallOrderMapper, MallOrder>
        implements MallOrderService {

    @Autowired
    private MallOrderItemService itemService;   // 订单明细
    @Autowired
    private  MallProductService productService; // 商品快照
    @Autowired
    private MallMerchantService merchantService;

    @Value("${spring.upload.server}")
    private String server;
    /**
     * 提交订单（事务）
     * @param dto 前端请求体
     * @return 订单主键 id
     */
    @Transactional(rollbackFor = Exception.class)
    public MallOrder createOrder(CreateOrderDTO dto) {
        /* 1. 取出前端传来的所有商品明细 */
        List<OrderItemDTO> items = dto.getItems();

        /* 2. 先准备一个累加器，用于计算整单实付总额 */
        BigDecimal orderTotal = BigDecimal.ZERO;

        /* 防御空指针 */
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("商品明细不能为空");
        }
        /* 3. 逐个商品做合法性检查 + 金额计算 */
        for (OrderItemDTO itemDto : items) {
            /* 3.1 根据商品 ID 查数据库 */
            MallProduct product = productService.getProductById(itemDto.getProductId());

            /* 3.2 如果商品不存在或已下架，直接抛异常给前端 */
            if (product == null || product.getStatus() != 1) {
                throw new IllegalArgumentException("商品不存在或已下架：" + itemDto.getProductId());
            }
            /* 3.3 拿到商品单价：优先用折扣价，没有再取原价 */
            String priceStr = StringUtils.hasText(product.getDiscountPrice())
                    ? product.getDiscountPrice()
                    : product.getPrice();
            BigDecimal unitPrice = new BigDecimal(priceStr);

            /* 3.4 累加本行小计：单价 × 数量 */
            orderTotal = orderTotal.add(unitPrice.multiply(BigDecimal.valueOf(itemDto.getQuantity())));
        }

        /* 4. 生成订单主表（一条订单） */
        MallOrder order = new MallOrder();
        String orderNo = "YL" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        order.setOrderNo(orderNo);          // 订单号YL+时间时分秒
        order.setUserId(9527L);                                   // 用户id
        order.setAddress(dto.getAddress());                      // 收货地址
        order.setPhone(dto.getPhone());                          // 收货电话
        order.setRemark(dto.getRemark());                        // 备注
        order.setTotalAmount(orderTotal.toPlainString());        // 把 BigDecimal 转成字符串
        order.setStatus(0);                                    // 0 表示“待支付”
        order.setMerchantId(dto.getMerchantId());               // 商铺id
        this.save(order);                                        // 插入主表
        /* 5. 把前端传来的多条明细，转成 List<OrderItem> */
        List<MallOrderItem> itemList = items.stream()
                .map(itemDto -> {
                    /* 5.1 再次查商品，确保名称是最新快照 */
                    MallProduct p = productService.getProductById(itemDto.getProductId());

                    /* 5.2 计算本行单价、总价（字符串形式） */
                    BigDecimal unitPrice = new BigDecimal(
                            StringUtils.hasText(p.getDiscountPrice())
                                    ? p.getDiscountPrice()
                                    : p.getPrice()
                    );
                    BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(itemDto.getQuantity()));

                    /* 5.3 封装成实体 */
                    MallOrderItem item = new MallOrderItem();
                    item.setOrderId(order.getId());           // 关联主订单
                    item.setProductId(p.getId());             // 商品ID
                    item.setProductName(p.getName());         // 商品快照名称
                    item.setQuantity(itemDto.getQuantity());  // 购买数量
                    item.setUnitPrice(unitPrice.toPlainString());  // 单价字符串
                    item.setTotalPrice(lineTotal.toPlainString()); // 小计字符串
                    return item;
                })
                .collect(Collectors.toList());

        /* 6. 批量插入订单明细 */
        itemService.saveBatch(itemList);

        /* 7. 把订单主键返回给前端 */
        return order;
    }

    /**
     * 逻辑删除订单
     * 不真正 DELETE，而是 UPDATE deleted = 1
     */
    public boolean removeOrderById(Long id) {
        /* 1. 先确保订单存在且未删除 */
        MallOrder exist = lambdaQuery()
                .eq(MallOrder::getId, id)
                .eq(MallOrder::getDeleted, 0)
                .one();
        if (exist == null) {
            return false;
        }
        /* 2. 软删主表 */
        return lambdaUpdate()
                .set(MallOrder::getDeleted, 1)   // 逻辑删除标记
                .eq(MallOrder::getId, id)
                .update();
    }
    /**
     * 修改订单白名单字段
     * 同时过滤已逻辑删除的订单
     */
    public boolean updateOrderById(MallOrder dto) {
        /* 1. 查询条件：订单存在且未删除 */
        boolean exists = lambdaQuery()
                .eq(MallOrder::getId, dto.getId())
                .eq(MallOrder::getDeleted, 0)
                .count() > 0;
        if (!exists) {
            return false;
        }

        /* 2. 执行更新（只会更新白名单字段） */
        return updateById(dto);
    }

    @Override
    public MallOrder getOrderById(Long id) {
        MallOrder byId = getById(id);
        return byId;
    }

    /**
     * 按商位查订单（逻辑删除过滤）
     */
    /*public List<MallOrder> listOrdersByMerchantId(Long merchantId,
                                                  String phone,
                                                  String status) {
        LambdaQueryWrapper<MallOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MallOrder::getDeleted, 0);

        if (merchantId != null) {
            wrapper.eq(MallOrder::getMerchantId, merchantId);
        }
        if (phone != null && !phone.trim().isEmpty()) {
            wrapper.eq(MallOrder::getPhone, phone.trim());
        }
        // 单状态或多状态（逗号分隔）
        if (status != null && !status.trim().isEmpty()) {
            if (status.contains(",")) {
                List<String> list = Arrays.asList(status.split(","));
                wrapper.in(MallOrder::getStatus, list);
            } else {
                wrapper.eq(MallOrder::getStatus, status.trim());
            }
        }
        wrapper.orderByDesc(MallOrder::getCreateTime);
        return list(wrapper);
    }*/

    /**
     * 一次性查询所有订单（或按商位/手机号/状态筛选）
     * 特点：不分页、零 N+1、毫秒级返回
     */
    public List<OrderListVO> listOrdersNoPage(Long merchantId,
                                              String phone,
                                              String status) {
        // 主查询：只查逻辑删除为 0 的订单
        LambdaQueryWrapper<MallOrder> w = new LambdaQueryWrapper<>();
        w.eq(MallOrder::getDeleted, 0);
        // 可选条件：商位 ID（不传则查全部）
        if (merchantId != null) w.eq(MallOrder::getMerchantId, merchantId);
        // 可选条件：手机号（支持空格）
        if (phone != null && !phone.trim().isEmpty()) w.eq(MallOrder::getPhone, phone.trim());
        // 可选条件：状态（支持单值或多值逗号分隔）
        if (status != null && !status.trim().isEmpty()) {
            if (status.contains(",")) {
                // 多状态：IN (1,2)
                w.in(MallOrder::getStatus, Arrays.asList(status.split(",")));
            } else {
                // 单状态：= 1
                w.eq(MallOrder::getStatus, status.trim());
            }
        }
        // 排序：最新订单在前
        w.orderByDesc(MallOrder::getCreateTime);
        // 执行查询（一条 SQL，走索引）
        List<MallOrder> orders = this.list(w);
        // 空结果直接返回
        if (orders.isEmpty()) return Collections.emptyList();
        // 收集所有订单的商位 ID（去重）
        Set<Long> merchantIds = orders.stream()
                .map(MallOrder::getMerchantId)
                .collect(Collectors.toSet());
        // 一次性查商位 Map（merchantId -> merchantName）
        Map<Long, String> merchantMap = merchantService.lambdaQuery()
                .select(MallMerchant::getId, MallMerchant::getName)
                .in(MallMerchant::getId, merchantIds)
                .list()
                .stream()
                .collect(Collectors.toMap(MallMerchant::getId, MallMerchant::getName));

        // 收集所有订单主键（用于批量明细）
        Set<Long> orderIds = orders.stream()
                .map(MallOrder::getId)
                .collect(Collectors.toSet());
        // 批量查明细（一次 SQL，按 orderId 分组）
        Map<Long, List<MallOrderItem>> itemMap = itemService.lambdaQuery()
                .in(MallOrderItem::getOrderId, orderIds)
                .list()
                .stream()
                .collect(Collectors.groupingBy(MallOrderItem::getOrderId));
        // 收集所有商品 ID（用于批量封面）
        Set<Long> productIds = itemMap.values()
                .stream()
                .flatMap(List::stream)
                .map(MallOrderItem::getProductId)
                .collect(Collectors.toSet());
        // 批量查封面（一次 SQL）
        Map<Long, String> coverMap = productService.lambdaQuery()
                .select(MallProduct::getId, MallProduct::getCoverImg)
                .in(MallProduct::getId, productIds)
                .list()
                .stream()
                .collect(Collectors.toMap(MallProduct::getId, MallProduct::getCoverImg));

        // 组装 VO（内存 O(n)）
        return orders.stream()
                .map(order -> buildOrderListVO(order, itemMap, coverMap, merchantMap))
                .collect(Collectors.toList());
    }

    /**
     * 组装 VO：把订单 + 明细 + 商品 + 商位 打包
     */
    private OrderListVO buildOrderListVO(MallOrder order,
                                         Map<Long, List<MallOrderItem>> itemMap,
                                         Map<Long, String> coverMap,
                                         Map<Long, String> merchantMap) {
        OrderListVO vo = new OrderListVO();
        BeanUtils.copyProperties(order, vo);
        // 真实商位名称
        vo.setMerchantName(merchantMap.get(order.getMerchantId()));
        // 明细列表
        List<OrderItemVO> items = itemMap.getOrDefault(order.getId(), Collections.emptyList())
                .stream()
                .map(item -> {
                    OrderItemVO itemVo = new OrderItemVO();
                    BeanUtils.copyProperties(item, itemVo);
                    itemVo.setCoverImg(server+coverMap.getOrDefault(item.getProductId(), ""));
                    return itemVo;
                })
                .collect(Collectors.toList());
        vo.setItems(items);
        return vo;
    }
}