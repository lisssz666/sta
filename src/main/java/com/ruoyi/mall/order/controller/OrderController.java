package com.ruoyi.mall.order.controller;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ruoyi.framework.web.controller.BaseController;
import com.ruoyi.framework.web.domain.AjaxResult;
import com.ruoyi.mall.merchant.domain.MallMerchant;
import com.ruoyi.mall.merchant.service.MallMerchantService;
import com.ruoyi.mall.order.domain.CreateOrderDTO;
import com.ruoyi.mall.order.domain.MallOrder;
import com.ruoyi.mall.order.domain.MallOrderItem;
import com.ruoyi.mall.order.domain.vo.OrderItemVO;
import com.ruoyi.mall.order.domain.vo.OrderListVO;
import com.ruoyi.mall.order.service.MallOrderItemService;
import com.ruoyi.mall.order.service.MallOrderService;
import com.ruoyi.mall.product.domain.MallProduct;
import com.ruoyi.mall.product.service.MallProductService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

import static com.ruoyi.framework.datasource.DynamicDataSourceContextHolder.log;

/**
 * 球队分组控制器
 */
@RestController
@RequestMapping("/order")
public class OrderController extends BaseController {


    @Autowired
    private  MallOrderService orderService;
    @Autowired
    private MallOrderItemService itemService;
    @Autowired
    private MallProductService productService;
    @Autowired
    private MallMerchantService mallMerchantService; // 商品快照


    /**
     * 创建订单
     */
    @PostMapping("/create")
    public AjaxResult create2(@RequestBody CreateOrderDTO dto) {
        System.out.println("dto:::: "+dto);
        return AjaxResult.success(orderService.createOrder(dto));
    }
    /**
     * 删除订单（软删）
     * 前端只传 id，后台把 deleted 置 1
     */
    @DeleteMapping("/delete")
    public AjaxResult delOrder(@RequestParam Long id) {
        /* 1. 参数校验 */
        if (id == null || id <= 0) {
            return AjaxResult.error("订单ID非法");
        }
        /* 2. 调用软删逻辑 */
        boolean success = orderService.removeOrderById(id);
        /* 3. 根据结果返回 */
        return success ? AjaxResult.success("订单已逻辑删除")
                : AjaxResult.error("删除失败，订单可能不存在或已删除");
    }

    /**
     * 修改订单（仅允许改地址/备注）
     * 防止前端篡改金额，只允许白名单字段
     */
    @PutMapping("/update")
    public AjaxResult updOrder(MallOrder dto) {
        /* 1. 必填参数校验 */
        if (dto.getId() == null || dto.getId() <= 0) {
            return AjaxResult.error("订单ID不能为空");
        }
        /* 2. 只允许修改的字段（白名单） */
        MallOrder updateObj = new MallOrder();
        updateObj.setId(dto.getId());
        if (StringUtils.hasText(dto.getAddress())) {
            updateObj.setAddress(dto.getAddress());
        }
        if (StringUtils.hasText(dto.getRemark())) {
            updateObj.setRemark(dto.getRemark());
        }
        if (dto.getStatus() != null) {                 // ✅ 修改状态
            updateObj.setStatus(dto.getStatus());
        }
        /* 3. 执行更新（逻辑删除过滤） */
        boolean success = orderService.updateOrderById(updateObj);
        /* 4. 返回结果 */
        return success ? AjaxResult.success("订单已更新")
                : AjaxResult.error("订单不存在或已删除");
    }
    /**
     * 订单列表（可按商位筛选）
     * GET /api/order/list?merchantId=1
     * 返回：订单主表 + 明细列表 + 商品封面绝对路径
     */
    @GetMapping("/list")
    public AjaxResult listOrder(@RequestParam(required = false) Long merchantId,
                                @RequestParam(required = false) String phone,
                                @RequestParam(required = false) String status
                                ) {
        List<OrderListVO>  page = orderService.listOrdersNoPage(merchantId, phone, status);
        return AjaxResult.success(page);
    }

    /**
     * 订单详情
     * GET /api/order/getOrderById?id=1
     * 返回：订单主表 + 明细列表 + 商品封面绝对路径
     */
    @GetMapping("/getOrderById")
    public AjaxResult getOrder(Long id) {
        // 1. 订单主表
        MallOrder order = orderService.getOrderById(id);
        if (order == null) {
            return AjaxResult.error("订单不存在");
        }
        // 查询商家信息
        MallMerchant merchant = mallMerchantService.getMerchantById(order.getMerchantId());
        if (merchant == null) {
            return AjaxResult.error("商位不存在");
        }
        // 2. 补充明细 & 封面图
        OrderListVO vo = buildOrderDetailVO(order,merchant.getName());
        return AjaxResult.success(vo);
    }

    /* ------------------ 私有工具方法 ------------------ */

    private OrderListVO buildOrderListVO(MallOrder order, String merchantName) {
        OrderListVO vo = new OrderListVO();
        BeanUtils.copyProperties(order, vo);
        vo.setMerchantName(merchantName);  // 设置商家名称
        // 明细
        List<MallOrderItem> items = itemService.listItemsByOrderId(order.getId());
        List<OrderItemVO> itemVos = items.stream()
                .map(item -> {
                    OrderItemVO itemVo = new OrderItemVO();
                    BeanUtils.copyProperties(item, itemVo);
                    // 商品封面补全
                    MallProduct product = productService.getProductById(item.getProductId());
                    if (product != null && product.getCoverImg() != null) {
                        itemVo.setCoverImg(product.getCoverImg());
                    }
                    return itemVo;
                })
                .collect(Collectors.toList());

        vo.setItems(itemVos);
        return vo;
    }
    private OrderListVO buildOrderDetailVO(MallOrder order,String name) {
        // 与 OrderListVO 结构一样，直接复用
        return buildOrderListVO(order,name);
    }
}