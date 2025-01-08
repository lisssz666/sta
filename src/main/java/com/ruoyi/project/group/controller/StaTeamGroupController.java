package com.ruoyi.project.group.controller;
import com.ruoyi.framework.web.controller.BaseController;
import com.ruoyi.framework.web.domain.AjaxResult;
import com.ruoyi.project.group.domain.StaTeamGroup;
import com.ruoyi.project.group.domain.dto.AddTeamsRequest;
import com.ruoyi.project.group.service.StaTeamGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 球队分组控制器
 */
@RestController
@RequestMapping("/teamGroup")
public class StaTeamGroupController extends BaseController {

    @Autowired
    private StaTeamGroupService teamGroupService;

    /**
     * 新建球队分组
     */
    @PostMapping("/add")
    public AjaxResult createTeamGroup(StaTeamGroup teamGroup) {
        return toAjax(teamGroupService.insertTeamGroup(teamGroup));
    }

    /**
     * 编辑球队分组
     */
    @PutMapping("/update")
    public AjaxResult editTeamGroup(StaTeamGroup teamGroup) {
        return toAjax(teamGroupService.updateTeamGroup(teamGroup));
    }

    /**
     * 删除球队分组
     */
    @DeleteMapping("/delete")
    public AjaxResult deleteTeamGroup(@RequestParam Long id) {
        return toAjax(teamGroupService.deleteTeamGroup(id));
    }

    /**
     * 根据联赛ID查询分组列表
     *
     * @param leagueId 联赛ID
     * @return 球队分组列表
     */
    @GetMapping("/list")
    public AjaxResult listTeamGroups(@RequestParam Long leagueId) {
        return AjaxResult.success(teamGroupService.selectTeamGroupsByLeagueId(leagueId));
    }


    /**
     * 新增多个球队ID到分组
     */
    @PostMapping("/addTeams")
    public AjaxResult addTeamsToGroup(AddTeamsRequest request) {
        return toAjax(teamGroupService.addTeamsToGroup(request));
    }

    /**
     * 删除多个球队ID从分组
     */
    @PostMapping("/removeTeam")
    public AjaxResult removeTeamsFromGroup(AddTeamsRequest request) {
        return toAjax(teamGroupService.removeTeamsFromGroup(request));
    }
}

