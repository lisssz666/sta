package com.ruoyi.project.rule.match.service;

import com.ruoyi.project.rule.match.domain.LeagueRule;
import java.util.List;

public interface LeagueRuleService {

    // 新增记录
    int addLeagueRule(LeagueRule league);

    // 根据ID查询记录
    LeagueRule getLeagueRuleById(Long leagueId);

    // 查询所有记录
    List<LeagueRule> getAllLeagueRules();
}
