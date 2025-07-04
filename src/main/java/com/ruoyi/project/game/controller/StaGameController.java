package com.ruoyi.project.game.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.project.game.enums.GameStage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.framework.aspectj.lang.annotation.Log;
import com.ruoyi.framework.aspectj.lang.enums.BusinessType;
import com.ruoyi.project.game.domain.StaGame;
import com.ruoyi.project.game.service.IStaGameService;
import com.ruoyi.framework.web.controller.BaseController;
import com.ruoyi.framework.web.domain.AjaxResult;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.framework.web.page.TableDataInfo;

/**
 * 比赛信息Controller
 * 
 * @author ruoyi
 * @date 2023-03-28
 */
@Api(tags = "比赛信息")
@RestController
@RequestMapping("/game")
public class StaGameController extends BaseController
{
    @Autowired
    private IStaGameService staGameService;

    /**
     * 查询比赛信息列表
     */
    @ApiOperation(value = "查询比赛信息列表")
    @GetMapping("/list")
    public TableDataInfo list(StaGame staGame)
    {
        startPage();
        List<StaGame> list = staGameService.selectStaGameList(staGame);
        return getDataTable(list);
    }

    /**
     * 获取比赛信息详细信息
     */
    @ApiOperation(value = "获取比赛信息详细信息")
    @GetMapping("/gameById")
    public AjaxResult getInfo(@RequestParam(value = "id") Long id)
    {
        return AjaxResult.success(staGameService.selectStaGameById(id));
    }

    /**
     * 新增比赛信息
     */
    @ApiOperation(value = "新增比赛信息")
    @PostMapping("/add")
    public AjaxResult add(StaGame staGame)
    {
        return staGameService.insertStaGame(staGame);
    }

    /**
     * 修改比赛信息
     */
    @ApiOperation(value = "修改比赛信息")
    @PutMapping("/edit")
    public AjaxResult edit(StaGame staGame) throws Exception {
        return AjaxResult.success(staGameService.updateStaGame(staGame));
    }

    /**
     * 删除比赛信息
     */
    @ApiOperation(value = "删除比赛信息")
    @PostMapping("/delete")
    public AjaxResult remove(@RequestParam(value = "ids") Long[] ids)
    {
        return toAjax(staGameService.deleteStaGameByIds(ids));
    }

    /**
     * 查询所有比赛阶段
     */
    @GetMapping("/stages")
    public List<String> getAllGameStages() {
        return Arrays.stream(GameStage.values())
                .map(GameStage::getDescription) // 获取中文描述
                .collect(Collectors.toList());
    }
}
