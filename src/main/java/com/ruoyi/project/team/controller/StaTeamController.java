package com.ruoyi.project.team.controller;

import java.io.IOException;
import java.util.List;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.framework.aspectj.lang.annotation.Log;
import com.ruoyi.framework.aspectj.lang.enums.BusinessType;
import com.ruoyi.project.team.domain.StaTeam;
import com.ruoyi.project.team.service.IStaTeamService;
import com.ruoyi.framework.web.controller.BaseController;
import com.ruoyi.framework.web.domain.AjaxResult;
import com.ruoyi.framework.web.page.TableDataInfo;

/**
 * 球队信息Controller
 * 
 * @author ruoyi
 * @date 2023-03-28
 */
@Api(tags = "球队信息")
@RestController
@RequestMapping("/team")
public class StaTeamController extends BaseController
{
    @Autowired
    private IStaTeamService staTeamService;

    /**
     * 查询球队信息列表
     */
    @ApiOperation(value = "查询球队信息列表")
    @GetMapping("/list")
    public TableDataInfo list(StaTeam staTeam)
    {
        startPage();
        List<StaTeam> list = staTeamService.selectStaTeamList(staTeam);
        return getDataTable(list);
    }

    /**
     * 获取球队信息详细信息
     */
    @ApiOperation(value = "获取球队信息详细信息")
    @GetMapping(value = "/getTeamById")
    public AjaxResult getInfo(Long id)
    {
        return AjaxResult.success(staTeamService.selectStaTeamById(id));
    }

    /**
     * 新增球队信息
     */
    @ApiOperation(value = "新增球队信息")
    @PostMapping("/add")
    public AjaxResult add(StaTeam staTeam) throws IOException
    {
        return AjaxResult.success(staTeamService.insertStaTeam(staTeam));
    }

    /**
     * 修改球队信息
     */
    @ApiOperation(value = "修改球队信息")
    @Log(title = "球队信息", businessType = BusinessType.UPDATE)
    @PutMapping("/edit")
    public AjaxResult edit(StaTeam staTeam) throws IOException
    {
        return toAjax(staTeamService.updateStaTeam(staTeam));
    }

    /**
     * 删除球队信息
     */
    @ApiOperation(value = "删除球队信息")
    @PostMapping("/delete")
    public AjaxResult remove(@RequestParam(value = "ids") Long[] ids)
    {
        return toAjax(staTeamService.deleteStaTeamByIds(ids));
    }
}
