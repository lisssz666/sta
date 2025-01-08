package com.ruoyi.project.rank.mapper;

import com.ruoyi.project.rank.domain.StaPlayerRanking;
import com.ruoyi.project.rank.domain.StaTeamRanking;
import com.ruoyi.project.statistic.domain.StaBestOfGame;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.HashMap;
import java.util.List;

/**
 * 球队榜Mapper
 */
public interface StaTeamRankingMapper {

    /**
     * 根据联赛ID查询球队榜
     *
     * @param leagueId 联赛ID
     * @return 球队榜列表
     */
    List<StaTeamRanking> selectByLeagueId(Long leagueId);

    /**
     * 根据联赛ID查询球员榜
     * @param leagueId 联赛ID
     * @return 球员榜列表
     */
    List<StaPlayerRanking> selectPlayersByLeagueId(@Param("leagueId") Long leagueId);

    /**
     * 根据比赛ID查询球员本场最佳
     */
    List<StaBestOfGame> getBestOfGame(Long compeid,Long homeid,Long awayid);
}
