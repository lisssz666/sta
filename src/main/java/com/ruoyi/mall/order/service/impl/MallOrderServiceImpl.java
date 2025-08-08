package com.ruoyi.mall.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.core.lang.UUID;
import com.ruoyi.mall.order.domain.CreateOrderDTO;
import com.ruoyi.mall.order.domain.MallOrder;
import com.ruoyi.mall.order.domain.MallOrderItem;
import com.ruoyi.mall.order.domain.OrderItemDTO;
import com.ruoyi.mall.order.mapper.MallOrderMapper;
import com.ruoyi.mall.order.service.MallOrderItemService;
import com.ruoyi.mall.order.service.MallOrderService;
import com.ruoyi.mall.product.domain.MallProduct;
import com.ruoyi.mall.product.service.MallProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

// 订单实现
@Service
public class MallOrderServiceImpl extends ServiceImpl<MallOrderMapper, MallOrder>
        implements MallOrderService {

    @Autowired
    private MallOrderItemService itemService;   // 订单明细
    @Autowired
    private  MallProductService productService; // 商品快照

    /**
     * 提交订单（事务）
     * @param dto 前端请求体
     * @return 订单主键 id
     */
    @Transactional(rollbackFor = Exception.class)
    public Long createOrder(CreateOrderDTO dto) {

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
        order.setOrderNo(UUID.randomUUID().toString());          // 随机订单号
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
        return order.getId();
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
        return getById(id);
    }

    /**
     * 按商位查订单（逻辑删除过滤）
     */
    public List<MallOrder> listOrdersByMerchantId(Long merchantId) {
        LambdaQueryWrapper<MallOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MallOrder::getDeleted, 0);            // 逻辑删除过滤
        if (merchantId != null) {
            wrapper.eq(MallOrder::getMerchantId, merchantId);
        }
        wrapper.orderByDesc(MallOrder::getCreateTime);
        return list(wrapper);
    }
}