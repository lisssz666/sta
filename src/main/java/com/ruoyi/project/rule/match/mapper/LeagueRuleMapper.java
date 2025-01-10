package com.ruoyi.project.rule.match.mapper;
import com.ruoyi.project.rule.enroll.domain.StaEnroll;
import com.ruoyi.project.rule.match.domain.LeagueRule;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;
import java.util.Optional;

@Mapper
public interface LeagueRuleMapper {

    // 新增记录
    int insertLeague(LeagueRule league);

    // 根据ID查询记录
    LeagueRule selectLeagueById(Long leagueId);

    // 查询所有记录
    List<LeagueRule> selectAllLeagues();

    // 修改记录
    Optional<StaEnroll> updateLeagueRule(LeagueRule league);
}
