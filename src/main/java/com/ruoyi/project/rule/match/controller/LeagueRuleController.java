package com.ruoyi.project.rule.match.controller;

import com.ruoyi.framework.web.controller.BaseController;
import com.ruoyi.framework.web.domain.AjaxResult;
import com.ruoyi.project.rule.match.domain.LeagueRule;
import com.ruoyi.project.rule.match.service.LeagueRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/leagueRules")
public class LeagueRuleController extends BaseController {

    @Autowired
    private LeagueRuleService leagueRuleService;

    // 新增记录
    @PostMapping("/add")
    public AjaxResult addLeagueRule(LeagueRule league) {
        return toAjax(leagueRuleService.addLeagueRule(league));
    }

    // 根据ID查询记录
    @GetMapping("/leagueRulesById")
    public AjaxResult getLeagueRuleById( Long leagueId) {
        return AjaxResult.success(leagueRuleService.getLeagueRuleById(leagueId));
    }

    // 查询所有记录
    @GetMapping("/list")
    public AjaxResult getAllLeagueRules() {
        return AjaxResult.success(leagueRuleService.getAllLeagueRules());
    }
}
