package com.ruoyi.project.game.organize.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.framework.web.controller.BaseController;
import com.ruoyi.framework.web.domain.AjaxResult;
import com.ruoyi.project.game.organize.domain.StaOrganizeGameEntity;
import com.ruoyi.project.game.organize.service.IStaOrganizeGameService;
import com.ruoyi.project.game.organize.util.RequestParamParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 组织比赛Controller
 * 支持新的数据结构：前备注、球队数组(JSON)、后备注
 */
@RestController
@RequestMapping("/game/organize")
public class StaOrganizeGameController extends BaseController {
    @Autowired
    private IStaOrganizeGameService service;

    /**
     * 查询组织比赛列表
     * 返回包含前备注、球队数组和后备注的数据结构
     */
    @GetMapping("/list")
    public AjaxResult list(StaOrganizeGameEntity entity) {
        try {
            // 调用Service层获取组织比赛列表
            List<StaOrganizeGameEntity> organizeGames = service.list(entity);
            
            // 返回完整的记录列表
            return AjaxResult.success(organizeGames);
        } catch (Exception e) {
            logger.error("查询组织比赛失败", e);
            return AjaxResult.error("查询失败: " + e.getMessage());
        }
    }

    @Autowired
    private ObjectMapper objectMapper;

    // 请求参数解析器实例
    private RequestParamParser requestParamParser;

    /**
     * 初始化方法，创建RequestParamParser实例
     */
    @Autowired
    public void init() {
        this.requestParamParser = new RequestParamParser(objectMapper);
    }

    /**
     * 新增组织比赛 - 万能方法
     * 支持JSON请求体、Form表单提交和URL参数
     */
    @PostMapping("/add")
    public AjaxResult add(@RequestBody(required = false) Map<String, Object> jsonBody,
                          @RequestParam(required = false) Map<String, String> queryParams,
                          @RequestHeader(required = false) String contentType,
                          HttpServletRequest request) {
        try {
            // 使用万能参数解析器解析请求参数
            Map<String, Object> requestData = requestParamParser.parseRequestParams(
                    jsonBody, queryParams, contentType, request);

            // 打印最终合并后的请求参数
            logger.info("新增组织比赛最终请求参数：{}", requestData);

            // 验证请求数据
            if (requestData.isEmpty()) {
                return AjaxResult.error("请求数据不能为空");
            }

            // 调用Service层添加组织比赛
            boolean saved = service.addOrganizeGame(requestData);
            
            if (saved) {
                return AjaxResult.success("保存成功");
            } else {
                return AjaxResult.error("保存失败");
            }
        } catch (IllegalArgumentException e) {
            return AjaxResult.error(e.getMessage());
        } catch (Exception e) {
            logger.error("新增组织比赛失败", e);
            return AjaxResult.error("操作失败: " + e.getMessage());
        }
    }

    /**
     * 重发接口：将创建时间更新为当前时间
     */
    @PutMapping("/repostById")
    public AjaxResult repost(Long id) {
        try {
            // 调用Service层重发组织比赛
            boolean updated = service.repostOrganizeGame(id);
            
            if (updated) {
                return AjaxResult.success("重发成功");
            } else {
                return AjaxResult.error("重发失败");
            }
        } catch (IllegalArgumentException e) {
            return AjaxResult.error(e.getMessage());
        } catch (Exception e) {
            logger.error("重发组织比赛失败", e);
            return AjaxResult.error("操作失败: " + e.getMessage());
        }
    }

    /**
     * 更新状态
     */
    @PutMapping("/updateStatusById")
    public AjaxResult updateStatus(StaOrganizeGameEntity organizeGame) {
        try {
            // 验证请求数据
            if (organizeGame == null || organizeGame.getId() == null || organizeGame.getStatus() == null) {
                return AjaxResult.error("请求数据不完整，缺少id或status字段");
            }
            
            // 调用Service层更新状态
            boolean updated = service.updateOrganizeGameStatus(organizeGame.getId(), organizeGame.getStatus());
            
            if (updated) {
                return AjaxResult.success("状态更新成功");
            } else {
                return AjaxResult.error("状态更新失败");
            }
        } catch (IllegalArgumentException e) {
            return AjaxResult.error(e.getMessage());
        } catch (Exception e) {
            logger.error("修改组织比赛状态失败", e);
            return AjaxResult.error("操作失败: " + e.getMessage());
        }
    }

    /**
     * 更新组织比赛
     */
    @PutMapping("/updateById")
    public AjaxResult update( Map<String, Object> requestData) {
        try {
            // 调用Service层更新组织比赛
            boolean updated = service.updateOrganizeGame(requestData);
            
            if (updated) {
                return AjaxResult.success("更新成功");
            } else {
                return AjaxResult.error("更新失败");
            }
        } catch (IllegalArgumentException e) {
            return AjaxResult.error(e.getMessage());
        } catch (Exception e) {
            logger.error("编辑组织比赛失败", e);
            return AjaxResult.error("操作失败: " + e.getMessage());
        }
    }

    /**
     * 删除组织比赛
     */
    @DeleteMapping("/deleteById/{id}")
    public AjaxResult delete(@PathVariable("id") Long id) {
        try {
            return toAjax(service.removeById(id));
        } catch (Exception e) {
            logger.error("删除组织比赛失败", e);
            return AjaxResult.error("删除失败: " + e.getMessage());
        }
    }
}
