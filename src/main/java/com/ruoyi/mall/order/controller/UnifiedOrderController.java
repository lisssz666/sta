package com.ruoyi.mall.order.controller;

import com.ruoyi.common.utils.TokenUtil;
import com.ruoyi.framework.web.controller.BaseController;
import com.ruoyi.framework.web.domain.AjaxResult;
import com.ruoyi.framework.web.page.TableDataInfo;
import com.ruoyi.mall.order.domain.*;
import com.ruoyi.mall.order.domain.vo.*;
import com.ruoyi.mall.order.service.LivePersonOrderService;
import com.ruoyi.mall.order.service.MallOrderService;
import com.ruoyi.mall.order.service.RefereeOrderService;
import com.ruoyi.mall.order.service.UnifiedOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 统一订单控制器
 * <p>
 * 整合裁判订单、直播订单、商品订单三大订单场景，通过 orderType 参数区分订单类型
 * 提供统一的创建、删除、修改、查询、取消等核心功能
 * </p>
 *
 * @author ruoyi
 * @version 1.0.0
 * @since 2026-05-13
 */
@RestController
@RequestMapping("/unified-order")
public class UnifiedOrderController extends BaseController {

    /**
     * 订单类型常量定义
     */
    private static final String ORDER_TYPE_REF = "REF";      // 裁判订单
    private static final String ORDER_TYPE_LIVE = "LIVE";    // 直播订单
    private static final String ORDER_TYPE_MALL = "MALL";    // 商品订单

    @Autowired
    private RefereeOrderService refereeOrderService;

    @Autowired
    private LivePersonOrderService livePersonOrderService;

    @Autowired
    private MallOrderService mallOrderService;

    @Autowired
    private UnifiedOrderService unifiedOrderService;

    /**
     * 从请求中获取用户ID
     * 优先从请求头的Authorization token中解析，其次从请求参数userId中获取
     *
     * @param request HTTP请求
     * @param userIdParam URL参数中的userId
     * @return 用户ID，如果无法获取返回null
     */
    private Long getUserId(HttpServletRequest request, Long userIdParam) {
        // 1. 优先从URL参数获取
        if (userIdParam != null && userIdParam > 0) {
            return userIdParam;
        }

        // 2. 从请求头的Authorization获取token
        String token = request.getHeader("Authorization");
        if (StringUtils.hasText(token)) {
            Long userId = TokenUtil.getUserIdFromToken(token);
            if (userId != null) {
                return userId;
            }
        }

        return null;
    }

    /**
     * 创建订单
     * <p>
     * 支持三种订单类型的统一创建接口
     * </p>
     *
     * @param request 创建订单请求，包含以下字段：
     *                - orderType：订单类型（必填）REF-裁判订单，LIVE-直播订单，MALL-商品订单
     *                - 其他参数根据订单类型不同而不同
     * @return 创建结果
     */
    @PostMapping("/create")
    public AjaxResult createOrder(@RequestBody UnifiedCreateOrderRequest request) {
        try {
            // 1. 参数校验
            String error = validateCreateRequest(request);
            if (StringUtils.hasText(error)) {
                return AjaxResult.error(error);
            }

            // 2. 根据订单类型调用不同的创建逻辑
            Object result;
            switch (request.getOrderType()) {
                case ORDER_TYPE_REF:
                    // 裁判订单创建
                    CreateRefereeOrderDTO dto = convertToRefereeDTO(request);
                    result = refereeOrderService.createRefereeOrder(
                            dto.getRefereeId(), dto.getContactName(), dto.getContactPhone(),
                            dto.getTotalAmount(), dto.getGameId(), dto.getMatchTime(),
                            dto.getRemark(), dto.getMatchInfo(), dto.getMatchLocation(), dto.getScheduleLog()
                    );
                    break;
                case ORDER_TYPE_LIVE:
                    // 直播订单创建
                    CreateLivePersonOrderDTO dtoLive = convertToLivePersonDTO(request);
                    result = livePersonOrderService.createLivePersonOrder(
                            dtoLive.getLivePersonId(), dtoLive.getContactName(), dtoLive.getContactPhone(),
                            dtoLive.getTotalAmount(), dtoLive.getGameId(), dtoLive.getMatchTime(),
                            dtoLive.getRemark(), dtoLive.getMatchInfo(), dtoLive.getMatchLocation()
                    );
                    break;
                case ORDER_TYPE_MALL:
                    // 商品订单创建
                    CreateOrderDTO dtoMall = convertToMallDTO(request);
                    result = mallOrderService.createOrder(dtoMall);
                    break;
                default:
                    throw new IllegalArgumentException("不支持的订单类型");
            }

            return AjaxResult.success(result);

        } catch (IllegalArgumentException e) {
            return AjaxResult.error(e.getMessage());
        } catch (Exception e) {
            return AjaxResult.error("创建订单失败：" + e.getMessage());
        }
    }

    /**
     * 删除订单
     * <p>
     * 支持三种订单类型的统一删除接口（软删除），用户只能删除自己的订单
     * </p>
     *
     * @param orderType 订单类型（必填）REF/LIVE/MALL
     * @param id 订单 ID（必填）
     * @param userId 用户ID（可选，优先从token解析）
     * @param request HTTP请求（用于获取token）
     * @return 删除结果
     */
    @DeleteMapping("/delete")
    public AjaxResult deleteOrder(@RequestParam String orderType,
                                  @RequestParam Long id,
                                  @RequestParam(required = false) Long userId,
                                  HttpServletRequest request) {
        try {
            // 获取用户ID
            Long currentUserId = getUserId(request, userId);
            if (currentUserId == null) {
                return AjaxResult.error("用户未登录");
            }

            // 1. 参数校验
            if (!validateOrderType(orderType)) {
                return AjaxResult.error("不支持的订单类型");
            }
            if (id == null || id <= 0) {
                return AjaxResult.error("订单 ID 非法");
            }

            // 2. 根据订单类型调用不同的删除逻辑
            boolean success;
            switch (orderType) {
                case ORDER_TYPE_REF:
                    success = refereeOrderService.removeOrderByUserId(currentUserId, id);
                    break;
                case ORDER_TYPE_LIVE:
                    success = livePersonOrderService.removeOrderByUserId(currentUserId, id);
                    break;
                case ORDER_TYPE_MALL:
                    success = mallOrderService.removeOrderByUserId(currentUserId, id);
                    break;
                default:
                    success = false;
                    break;
            }

            return success ? AjaxResult.success("订单已删除")
                    : AjaxResult.error("删除失败，订单可能不存在");

        } catch (Exception e) {
            return AjaxResult.error("删除失败：" + e.getMessage());
        }
    }

    /**
     * 修改订单
     * <p>
     * 支持三种订单类型的统一修改接口
     * </p>
     *
     * @param request 修改请求，包含：
     *                - orderType：订单类型（必填）
     *                - orderData：订单数据（必填）
     * @return 修改结果
     */
    @PutMapping("/update")
    public AjaxResult updateOrder(@RequestBody UnifiedOrderUpdateRequest request) {
        try {
            // 1. 参数校验
            if (!validateOrderType(request.getOrderType())) {
                return AjaxResult.error("不支持的订单类型");
            }
            if (request.getOrderData() == null) {
                return AjaxResult.error("订单数据不能为空");
            }

            // 2. 根据订单类型调用不同的更新逻辑
            boolean success;
            switch (request.getOrderType()) {
                case ORDER_TYPE_REF:
                    RefereeOrder orderRef = convertToRefereeOrder(request.getOrderData());
                    success = refereeOrderService.updateOrderById(orderRef);
                    break;
                case ORDER_TYPE_LIVE:
                    LivePersonOrder orderLive = convertToLivePersonOrder(request.getOrderData());
                    success = livePersonOrderService.updateOrderById(orderLive);
                    break;
                case ORDER_TYPE_MALL:
                    MallOrder orderMall = convertToMallOrder(request.getOrderData());
                    success = mallOrderService.updateOrderById(orderMall);
                    break;
                default:
                    success = false;
                    break;
            }

            return success ? AjaxResult.success("订单已更新")
                    : AjaxResult.error("订单不存在或更新失败");

        } catch (Exception e) {
            return AjaxResult.error("更新失败：" + e.getMessage());
        }
    }

    /**
     * 订单详情查询
     * <p>
     * 支持三种订单类型的统一详情查询接口，用户只能查询自己的订单
     * </p>
     *
     * @param orderType 订单类型（必填）
     * @param id 订单 ID（必填）
     * @param userId 用户ID（可选，优先从token解析）
     * @param request HTTP请求（用于获取token）
     * @return 订单详情
     */
    @GetMapping("/detail")
    public AjaxResult getOrderDetail(@RequestParam String orderType,
                                     @RequestParam Long id,
                                     @RequestParam(required = false) Long userId,
                                     HttpServletRequest request) {
        try {
            // 获取用户ID
            Long currentUserId = getUserId(request, userId);
            if (currentUserId == null) {
                return AjaxResult.error("用户未登录");
            }

            // 1. 参数校验
            if (!validateOrderType(orderType)) {
                return AjaxResult.error("不支持的订单类型");
            }
            if (id == null || id <= 0) {
                return AjaxResult.error("订单 ID 非法");
            }

            // 2. 根据订单类型调用不同的查询逻辑
            Object vo;
            switch (orderType) {
                case ORDER_TYPE_REF:
                    vo = refereeOrderService.getOrderVOByUserId(currentUserId, id);
                    break;
                case ORDER_TYPE_LIVE:
                    vo = livePersonOrderService.getOrderVOByUserId(currentUserId, id);
                    break;
                case ORDER_TYPE_MALL:
                    vo = mallOrderService.getOrderVOByUserId(currentUserId, id);
                    break;
                default:
                    throw new IllegalArgumentException("不支持的订单类型");
            }

            if (vo == null) {
                return AjaxResult.error("订单不存在或无权限访问");
            }

            return AjaxResult.success(vo);

        } catch (Exception e) {
            return AjaxResult.error("查询失败：" + e.getMessage());
        }
    }

    /**
     * 取消订单
     * <p>
     * 支持三种订单类型的统一取消接口，用户只能取消自己的订单
     * </p>
     *
     * @param orderType 订单类型（必填）REF/LIVE/MALL
     * @param id 订单 ID（必填）
     * @param userId 用户ID（可选，优先从token解析）
     * @param request HTTP请求（用于获取token）
     * @return 取消结果
     */
    @PostMapping("/cancel")
    public AjaxResult cancelOrder(@RequestParam String orderType,
                                  @RequestParam Long id,
                                  @RequestParam(required = false) Long userId,
                                  HttpServletRequest request) {
        try {
            // 获取用户ID
            Long currentUserId = getUserId(request, userId);
            if (currentUserId == null) {
                return AjaxResult.error("用户未登录");
            }

            // 1. 参数校验
            if (!validateOrderType(orderType)) {
                return AjaxResult.error("不支持的订单类型");
            }
            if (id == null || id <= 0) {
                return AjaxResult.error("订单 ID 非法");
            }

            // 2. 根据订单类型调用不同的取消逻辑
            boolean result;
            switch (orderType) {
                case ORDER_TYPE_REF:
                    result = refereeOrderService.cancelOrderByUserId(currentUserId, id);
                    break;
                case ORDER_TYPE_LIVE:
                    result = livePersonOrderService.cancelOrderByUserId(currentUserId, id);
                    break;
                case ORDER_TYPE_MALL:
                    result = mallOrderService.cancelOrderByUserId(currentUserId, id);
                    break;
                default:
                    result = false;
                    break;
            }

            return result ? AjaxResult.success("订单已取消")
                    : AjaxResult.error("取消订单失败");

        } catch (IllegalArgumentException e) {
            return AjaxResult.error(e.getMessage());
        } catch (Exception e) {
            return AjaxResult.error("取消订单失败：" + e.getMessage());
        }
    }

    /**
     * 统一订单查询接口
     * <p>
     * 支持跨订单类型的统一查询，可指定订单类型或查询所有类型
     * </p>
     *
     * @param orderType 订单类型（可选）REF/LIVE/MALL，不传则查询所有类型
     * @param status 订单状态（可选）0-未付款 1-已付款 2-已取消 3-已完成
     * @param orderNo 订单号（可选，模糊匹配）
     * @param contactName 联系人姓名（可选，模糊匹配）
     * @return 订单列表
     */
    @GetMapping("/unified/list")
    public TableDataInfo listUnifiedOrders(@RequestParam(required = false) String orderType,
                                          @RequestParam(required = false) Integer status,
                                          @RequestParam(required = false) String orderNo,
                                          @RequestParam(required = false) String contactName,
                                          @RequestParam(required = false) Long userId,
                                          HttpServletRequest request) {
        try {
            // 获取用户ID（优先从参数获取，其次从token解析）
            Long currentUserId = getUserId(request, userId);
            if (currentUserId == null) {
                // 用户未登录或未传递userId，返回空列表
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(401);
                rspData.setMsg("用户未登录");
                rspData.setData(new ArrayList<>());
                rspData.setTotal(0L);
                return rspData;
            }
            
            startPage();
            UnifiedOrderQueryDTO queryDTO = new UnifiedOrderQueryDTO();
            queryDTO.setOrderType(orderType);
            queryDTO.setStatus(status);
            queryDTO.setOrderNo(orderNo);
            queryDTO.setContactName(contactName);
            queryDTO.setUserId(currentUserId);
            List<UnifiedOrderVO> list = unifiedOrderService.listUnifiedOrders(queryDTO);
            
            // 返回分页数据
            return getDataTable(list);
        } catch (Exception e) {
            logger.error("查询统一订单失败", e);
            TableDataInfo rspData = new TableDataInfo();
            rspData.setCode(500);
            rspData.setMsg("查询失败: " + e.getMessage());
            rspData.setData(new ArrayList<>());
            rspData.setTotal(0L);
            return rspData;
        }
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 校验创建订单请求参数
     */
    private String validateCreateRequest(UnifiedCreateOrderRequest request) {
        if (request.getOrderType() == null) {
            return "订单类型不能为空";
        }
        if (!validateOrderType(request.getOrderType())) {
            return "不支持的订单类型：" + request.getOrderType();
        }

        // 根据订单类型校验必填参数
        String validationResult;
        switch (request.getOrderType()) {
            case ORDER_TYPE_REF:
                validationResult = validateRefereeOrder(request);
                break;
            case ORDER_TYPE_LIVE:
                validationResult = validateLivePersonOrder(request);
                break;
            case ORDER_TYPE_MALL:
                validationResult = validateMallOrder(request);
                break;
            default:
                validationResult = "不支持的订单类型";
                break;
        }
        return validationResult;
    }

    /**
     * 校验订单类型是否合法
     */
    private boolean validateOrderType(String orderType) {
        return ORDER_TYPE_REF.equals(orderType) ||
               ORDER_TYPE_LIVE.equals(orderType) ||
               ORDER_TYPE_MALL.equals(orderType);
    }

    /**
     * 校验裁判订单参数
     */
    private String validateRefereeOrder(UnifiedCreateOrderRequest request) {
        if (request.getRefereeId() == null) {
            return "裁判 ID 不能为空";
        }
        if (StringUtils.isEmpty(request.getContactName())) {
            return "联系人姓名不能为空";
        }
        if (StringUtils.isEmpty(request.getContactPhone())) {
            return "联系人电话不能为空";
        }
        if (StringUtils.isEmpty(request.getTotalAmount())) {
            return "订单金额不能为空";
        }
        return "";
    }

    /**
     * 校验直播订单参数
     */
    private String validateLivePersonOrder(UnifiedCreateOrderRequest request) {
        if (request.getLivePersonId() == null) {
            return "直播人员 ID 不能为空";
        }
        if (StringUtils.isEmpty(request.getContactName())) {
            return "联系人姓名不能为空";
        }
        if (StringUtils.isEmpty(request.getContactPhone())) {
            return "联系人电话不能为空";
        }
        if (StringUtils.isEmpty(request.getTotalAmount())) {
            return "订单金额不能为空";
        }
        return "";
    }

    /**
     * 校验商品订单参数
     */
    private String validateMallOrder(UnifiedCreateOrderRequest request) {
        if (request.getUserId() == null) {
            return "用户 ID 不能为空";
        }
        if (request.getItems() == null || request.getItems().isEmpty()) {
            return "订单商品不能为空";
        }
        return "";
    }

    /**
     * 转换为裁判订单 DTO
     */
    private CreateRefereeOrderDTO convertToRefereeDTO(UnifiedCreateOrderRequest request) {
        CreateRefereeOrderDTO dto = new CreateRefereeOrderDTO();
        dto.setRefereeId(request.getRefereeId());
        dto.setContactName(request.getContactName());
        dto.setContactPhone(request.getContactPhone());
        dto.setTotalAmount(request.getTotalAmount());
        dto.setGameId(request.getGameId());
        dto.setMatchTime(request.getMatchTime());
        dto.setRemark(request.getRemark());
        dto.setMatchInfo(request.getMatchInfo());
        dto.setMatchLocation(request.getMatchLocation());
        dto.setScheduleLog(request.getScheduleLog());
        return dto;
    }

    /**
     * 转换为直播订单 DTO
     */
    private CreateLivePersonOrderDTO convertToLivePersonDTO(UnifiedCreateOrderRequest request) {
        CreateLivePersonOrderDTO dto = new CreateLivePersonOrderDTO();
        dto.setLivePersonId(request.getLivePersonId());
        dto.setContactName(request.getContactName());
        dto.setContactPhone(request.getContactPhone());
        dto.setTotalAmount(request.getTotalAmount());
        dto.setGameId(request.getGameId());
        dto.setMatchTime(request.getMatchTime());
        dto.setRemark(request.getRemark());
        dto.setMatchInfo(request.getMatchInfo());
        dto.setMatchLocation(request.getMatchLocation());
        return dto;
    }

    /**
     * 转换为商品订单 DTO
     */
    private CreateOrderDTO convertToMallDTO(UnifiedCreateOrderRequest request) {
        CreateOrderDTO dto = new CreateOrderDTO();
        dto.setPhone(request.getContactPhone());
        dto.setAddress(request.getAddress());
        dto.setRemark(request.getRemark());
        dto.setMerchantId(request.getMerchantId());
        // 转换商品列表
        if (request.getItems() != null) {
            List<OrderItemDTO> items = request.getItems().stream()
                    .map(item -> {
                        OrderItemDTO dtoItem = new OrderItemDTO();
                        dtoItem.setProductId(item.getProductId());
                        dtoItem.setQuantity(item.getQuantity());
                        return dtoItem;
                    })
                    .collect(java.util.stream.Collectors.toList());
            dto.setItems(items);
        }
        return dto;
    }

    /**
     * 从 Map 转换为裁判订单对象
     */
    private RefereeOrder convertToRefereeOrder(Map<String, Object> data) {
        RefereeOrder order = new RefereeOrder();
        if (data.containsKey("id")) order.setId(((Number) data.get("id")).longValue());
        if (data.containsKey("status")) order.setStatus((Integer) data.get("status"));
        if (data.containsKey("remark")) order.setRemark((String) data.get("remark"));
        return order;
    }

    /**
     * 从 Map 转换为直播订单对象
     */
    private LivePersonOrder convertToLivePersonOrder(Map<String, Object> data) {
        LivePersonOrder order = new LivePersonOrder();
        if (data.containsKey("id")) order.setId(((Number) data.get("id")).longValue());
        if (data.containsKey("status")) order.setStatus((Integer) data.get("status"));
        if (data.containsKey("remark")) order.setRemark((String) data.get("remark"));
        return order;
    }

    /**
     * 从 Map 转换为商品订单对象
     */
    private MallOrder convertToMallOrder(Map<String, Object> data) {
        MallOrder order = new MallOrder();
        if (data.containsKey("id")) order.setId(((Number) data.get("id")).longValue());
        if (data.containsKey("status")) order.setStatus((Integer) data.get("status"));
        if (data.containsKey("address")) order.setAddress((String) data.get("address"));
        if (data.containsKey("remark")) order.setRemark((String) data.get("remark"));
        return order;
    }

    /**
     * 从 JSON 字符串中获取 Long 参数
     */
    private Long getLongParam(String json, String key) {
        // 简化实现，实际可使用 JSON 解析库
        return null;
    }

    /**
     * 从 JSON 字符串中获取 String 参数
     */
    private String getStringParam(String json, String key) {
        // 简化实现，实际可使用 JSON 解析库
        return null;
    }

    /**
     * 从 JSON 字符串中获取 Integer 参数
     */
    private Integer getIntParam(String json, String key) {
        // 简化实现，实际可使用 JSON 解析库
        return null;
    }
}

/**
 * 统一创建订单请求
 */
class UnifiedCreateOrderRequest {
    private String orderType;          // 订单类型：REF/LIVE/MALL
    private Long refereeId;            // 裁判 ID（裁判订单用）
    private Long livePersonId;         // 直播人员 ID（直播订单用）
    private Long userId;               // 用户 ID（商品订单用）
    private String contactName;        // 联系人姓名
    private String contactPhone;       // 联系人电话
    private String totalAmount;        // 订单金额
    private Long gameId;               // 比赛 ID
    private String matchTime;          // 比赛时间
    private String remark;             // 备注
    private String matchInfo;          // 比赛信息
    private String matchLocation;      // 比赛地点
    private String scheduleLog;        // 日程日志（裁判订单用）
    private List<OrderItemRequest> items;  // 商品列表（商品订单用）
    private String address;            // 收货地址（商品订单用）
    private Long merchantId;            // 商铺id（商品订单用）

    // Getter 和 Setter
    public String getOrderType() { return orderType; }
    public void setOrderType(String orderType) { this.orderType = orderType; }
    public Long getRefereeId() { return refereeId; }
    public void setRefereeId(Long refereeId) { this.refereeId = refereeId; }
    public Long getLivePersonId() { return livePersonId; }
    public void setLivePersonId(Long livePersonId) { this.livePersonId = livePersonId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getContactName() { return contactName; }
    public void setContactName(String contactName) { this.contactName = contactName; }
    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }
    public String getTotalAmount() { return totalAmount; }
    public void setTotalAmount(String totalAmount) { this.totalAmount = totalAmount; }
    public Long getGameId() { return gameId; }
    public void setGameId(Long gameId) { this.gameId = gameId; }
    public String getMatchTime() { return matchTime; }
    public void setMatchTime(String matchTime) { this.matchTime = matchTime; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    public String getMatchInfo() { return matchInfo; }
    public void setMatchInfo(String matchInfo) { this.matchInfo = matchInfo; }
    public String getMatchLocation() { return matchLocation; }
    public void setMatchLocation(String matchLocation) { this.matchLocation = matchLocation; }
    public String getScheduleLog() { return scheduleLog; }
    public void setScheduleLog(String scheduleLog) { this.scheduleLog = scheduleLog; }
    public List<OrderItemRequest> getItems() { return items; }
    public void setItems(List<OrderItemRequest> items) { this.items = items; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public Long getMerchantId() { return merchantId; }
    public void setMerchantId(Long merchantId) { this.merchantId = merchantId; }
     
}

/**
 * 商品订单项请求
 */
class OrderItemRequest {
    private Long productId;    // 商品 ID
    private Integer quantity;  // 数量
    private String price;      // 单价

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public String getPrice() { return price; }
    public void setPrice(String price) { this.price = price; }
}

/**
 * 统一订单删除/取消请求
 */
class UnifiedOrderDeleteRequest {
    private String orderType;  // 订单类型
    private Long id;           // 订单 ID

    public String getOrderType() { return orderType; }
    public void setOrderType(String orderType) { this.orderType = orderType; }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
}

/**
 * 统一订单更新请求
 */
class UnifiedOrderUpdateRequest {
    private String orderType;              // 订单类型
    private Map<String, Object> orderData; // 订单数据

    public String getOrderType() { return orderType; }
    public void setOrderType(String orderType) { this.orderType = orderType; }
    public Map<String, Object> getOrderData() { return orderData; }
    public void setOrderData(Map<String, Object> orderData) { this.orderData = orderData; }
}
