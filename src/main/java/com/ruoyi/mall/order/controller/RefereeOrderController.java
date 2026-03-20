package com.ruoyi.mall.order.controller;
import com.ruoyi.framework.web.controller.BaseController;
import com.ruoyi.framework.web.domain.AjaxResult;
import com.ruoyi.mall.order.domain.CreateRefereeOrderDTO;
import com.ruoyi.mall.order.domain.RefereeOrder;
import com.ruoyi.mall.order.domain.vo.RefereeOrderVO;
import com.ruoyi.mall.order.service.RefereeOrderService;
import com.ruoyi.project.referee.domain.StaRefereeInfoEntity;
import com.ruoyi.project.referee.service.IStaRefereeInfoService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/referee/order")
public class RefereeOrderController extends BaseController {

    @Autowired
    private RefereeOrderService refereeOrderService;
    @Autowired
    private IStaRefereeInfoService refereeInfoService;

    /**
     * 创建裁判订单
     */
    @PostMapping("/create")
    public AjaxResult createRefereeOrder(@RequestBody CreateRefereeOrderDTO dto) {
        try {
            // 1. 参数校验
            if (dto.getRefereeId() == null) {
                return AjaxResult.error("裁判ID不能为空");
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
            RefereeOrder order = refereeOrderService.createRefereeOrder(
                    dto.getRefereeId(), dto.getContactName(), dto.getContactPhone(), dto.getTotalAmount(), 
                    dto.getGameId(), dto.getMatchTime(), dto.getRemark(), dto.getMatchInfo(), dto.getMatchLocation(), dto.getScheduleLog()
            );
            return AjaxResult.success(order);
        } catch (IllegalArgumentException e) {
            return AjaxResult.error(e.getMessage());
        } catch (Exception e) {
            return AjaxResult.error("创建订单失败：" + e.getMessage());
        }
    }

    /**
     * 删除裁判订单（软删）
     */
    @DeleteMapping("/delete")
    public AjaxResult delOrder(@RequestParam Long id) {
        /* 1. 参数校验 */
        if (id == null || id <= 0) {
            return AjaxResult.error("订单ID非法");
        }
        /* 2. 调用软删逻辑 */
        boolean success = refereeOrderService.removeOrderById(id);
        /* 3. 根据结果返回 */
        return success ? AjaxResult.success("订单已删除")
                : AjaxResult.error("删除失败，订单可能不存在");
    }

    /**
     * 修改裁判订单
     */
    @PutMapping("/update")
    public AjaxResult updOrder(@RequestBody RefereeOrder dto) {
        /* 1. 必填参数校验 */
        if (dto.getId() == null || dto.getId() <= 0) {
            return AjaxResult.error("订单ID不能为空");
        }
        /* 2. 执行更新 */
        boolean success = refereeOrderService.updateOrderById(dto);
        /* 3. 返回结果 */
        return success ? AjaxResult.success("订单已更新")
                : AjaxResult.error("订单不存在或更新失败");
    }

    /**
     * 裁判订单列表
     */
    @GetMapping("/list")
    public AjaxResult listOrder(@RequestParam(required = false) Long refereeId,
                               @RequestParam(required = false) String contactPhone,
                               @RequestParam(required = false) Integer status
                               ) {
        List<com.ruoyi.mall.order.domain.vo.RefereeOrderVO> list = refereeOrderService.listOrdersNoPage(refereeId, contactPhone, status);
        return AjaxResult.success(list);
    }

    /**
     * 裁判订单详情
     */
    @GetMapping("/detail")
    public AjaxResult getOrder(@RequestParam Long id) {
        // 查询订单VO（包含裁判头像）
        RefereeOrderVO vo = refereeOrderService.getOrderVOById(id);
        if (vo == null) {
            return AjaxResult.error("订单不存在");
        }
        return AjaxResult.success(vo);
    }
    
    /**
     * 取消裁判订单
     */
    @PostMapping("/cancel")
    public AjaxResult cancelOrder(@RequestParam Long id) {
        try {
            // 1. 参数校验
            if (id == null || id <= 0) {
                return AjaxResult.error("订单ID不能为空");
            }
            
            // 2. 调用Service取消订单
            boolean result = refereeOrderService.cancelOrder(id);
            
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
