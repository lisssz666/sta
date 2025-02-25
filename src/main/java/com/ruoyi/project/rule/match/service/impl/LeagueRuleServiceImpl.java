package com.ruoyi.project.rule.match.service.impl;

import com.ruoyi.project.rule.enroll.domain.StaEnroll;
import com.ruoyi.project.rule.match.domain.LeagueRule;
import com.ruoyi.project.rule.match.mapper.LeagueRuleMapper;
import com.ruoyi.project.rule.match.service.LeagueRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class LeagueRuleServiceImpl implements LeagueRuleService {

    @Autowired
    private LeagueRuleMapper leagueMapper;

    @Override
    public int addLeagueRule(LeagueRule league) {
        // 使用 leagueid 调用 getLeagueRuleById 判断是否存在
        Long leagueId = league.getLeagueId();
        LeagueRule existingLeagueRule = getLeagueRuleById(leagueId);
        if (existingLeagueRule != null) {
            // 如果存在，返回一个特定的值或抛出异常
            throw new IllegalStateException("联赛规则已存在: " + leagueId);
        }
        // 不存在，正常插入
        return leagueMapper.insertLeague(league);
    }

    @Override
    public LeagueRule getLeagueRuleById(Long leagueId) {
        return leagueMapper.selectLeagueById(leagueId);
    }

    @Override
    public List<LeagueRule> getAllLeagueRules() {
        return leagueMapper.selectAllLeagues();
    }

    @Override
    public LeagueRule updateLeagueRule(LeagueRule league) {
        return leagueMapper.updateLeagueRule(league);
    }
}
