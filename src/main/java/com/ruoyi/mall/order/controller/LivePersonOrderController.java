package com.ruoyi.mall.order.controller;

import com.ruoyi.framework.web.controller.BaseController;
import com.ruoyi.framework.web.domain.AjaxResult;
import com.ruoyi.framework.web.page.TableDataInfo;
import com.ruoyi.mall.order.domain.CreateLivePersonOrderDTO;
import com.ruoyi.mall.order.domain.LivePersonOrder;
import com.ruoyi.mall.order.domain.vo.LivePersonOrderVO;
import com.ruoyi.mall.order.service.LivePersonOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 直播人员订单Controller
 * 描述：提供直播人员订单的增删查接口
 * 
 * @author ruoyi
 * @version 1.0.0
 * @since 2026-04-27
 */
@RestController
@RequestMapping("/liveperson/order")
public class LivePersonOrderController extends BaseController {

    @Autowired
    private LivePersonOrderService livePersonOrderService;

    /**
     * 创建直播人员订单
     * <p>
     * 接收前端传来的订单信息，创建直播人员订单
     * 必填参数：livePersonId、contactName、contactPhone、totalAmount
     * 可选参数：gameId、matchTime、remark、matchInfo、matchLocation
     * </p>
     *
     * @param dto 创建订单DTO
     * @return 创建结果
     */
    @PostMapping("/create")
    public AjaxResult createLivePersonOrder(@RequestBody CreateLivePersonOrderDTO dto) {
        try {
            // 1. 参数校验
            if (dto.getLivePersonId() == null) {
                return AjaxResult.error("直播人员ID不能为空");
            }
            if (StringUtils.isEmpty(dto.getContactName())) {
                return AjaxResult.error("联系人姓名不能为空");
            }
            if (StringUtils.isEmpty(dto.getContactPhone())) {
                return AjaxResult.error("联系人电话不能为空");
            }
            if (StringUtils.isEmpty(dto.getTotalAmount())) {
                return AjaxResult.error("订单金额不能为空");
            }
            // 如果没有提供比赛ID，需要校验比赛地点
            if (dto.getGameId() == null) {
                if (StringUtils.isEmpty(dto.getMatchLocation())) {
                    return AjaxResult.error("比赛地点不能为空");
                }
            }

            // 2. 调用Service创建订单
            LivePersonOrder order = livePersonOrderService.createLivePersonOrder(
                    dto.getLivePersonId(), dto.getContactName(), dto.getContactPhone(), dto.getTotalAmount(),
                    dto.getGameId(), dto.getMatchTime(), dto.getRemark(), dto.getMatchInfo(), dto.getMatchLocation()
            );
            return AjaxResult.success(order);
        } catch (IllegalArgumentException e) {
            return AjaxResult.error(e.getMessage());
        } catch (Exception e) {
            return AjaxResult.error("创建订单失败：" + e.getMessage());
        }
    }

    /**
     * 删除直播人员订单（软删除）
     * <p>
     * 根据订单ID删除订单，实际执行逻辑删除
     * </p>
     *
     * @param id 订单ID
     * @return 删除结果
     */
    @DeleteMapping("/delete")
    public AjaxResult delOrder(@RequestParam Long id) {
        // 1. 参数校验
        if (id == null || id <= 0) {
            return AjaxResult.error("订单ID非法");
        }
        
        // 2. 调用软删逻辑
        boolean success = livePersonOrderService.removeOrderById(id);
        
        // 3. 根据结果返回
        return success ? AjaxResult.success("订单已删除")
                : AjaxResult.error("删除失败，订单可能不存在");
    }

    /**
     * 修改直播人员订单
     * <p>
     * 根据订单ID修改订单信息
     * </p>
     *
     * @param dto 订单信息
     * @return 修改结果
     */
    @PutMapping("/update")
    public AjaxResult updOrder(@RequestBody LivePersonOrder dto) {
        // 1. 必填参数校验
        if (dto.getId() == null || dto.getId() <= 0) {
            return AjaxResult.error("订单ID不能为空");
        }
        
        // 2. 执行更新
        boolean success = livePersonOrderService.updateOrderById(dto);
        
        // 3. 返回结果
        return success ? AjaxResult.success("订单已更新")
                : AjaxResult.error("订单不存在或更新失败");
    }

    /**
     * 直播人员订单列表（分页）
     * <p>
     * 根据条件查询订单列表，支持按直播人员ID、联系人电话、订单状态筛选
     * </p>
     *
     * @param livePersonId 直播人员ID（可选）
     * @param contactPhone 联系人电话（可选）
     * @param status 订单状态（可选）
     * @return 订单列表
     */
    @GetMapping("/list")
    public TableDataInfo listOrder(@RequestParam(required = false) Long livePersonId,
                                  @RequestParam(required = false) String contactPhone,
                                  @RequestParam(required = false) Integer status) {
        startPage();
        List<LivePersonOrderVO> list = livePersonOrderService.listOrdersNoPage(livePersonId, contactPhone, status);
        return getDataTable(list);
    }

    /**
     * 直播人员订单详情
     * <p>
     * 根据订单ID查询订单详情，包含直播人员头像等扩展信息
     * </p>
     *
     * @param id 订单ID
     * @return 订单详情
     */
    @GetMapping("/detail")
    public AjaxResult getOrder(@RequestParam Long id) {
        // 查询订单VO（包含直播人员头像）
        LivePersonOrderVO vo = livePersonOrderService.getOrderVOById(id);
        if (vo == null) {
            return AjaxResult.error("订单不存在");
        }
        return AjaxResult.success(vo);
    }

    /**
     * 取消直播人员订单
     * <p>
     * 取消订单，如果订单已付款则执行退款流程
     * </p>
     *
     * @param id 订单ID
     * @return 取消结果
     */
    @PostMapping("/cancel")
    public AjaxResult cancelOrder(@RequestParam Long id) {
        try {
            // 1. 参数校验
            if (id == null || id <= 0) {
                return AjaxResult.error("订单ID不能为空");
            }

            // 2. 调用Service取消订单
            boolean result = livePersonOrderService.cancelOrder(id);

            // 3. 返回结果
            return result ? AjaxResult.success("订单已取消")
                    : AjaxResult.error("取消订单失败");
        } catch (IllegalArgumentException e) {
            return AjaxResult.error(e.getMessage());
        } catch (Exception e) {
            return AjaxResult.error("取消订单失败：" + e.getMessage());
        }
    }
}
