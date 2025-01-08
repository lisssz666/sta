package com.ruoyi.project.rank.service;
import com.ruoyi.project.rank.domain.StaPlayerRanking;
import com.ruoyi.project.rank.domain.StaTeamRanking;
import com.ruoyi.project.statistic.domain.StaBestOfGame;

import java.util.HashMap;
import java.util.List;

/**
 * 球队榜Service接口
 */
public interface IStaTeamRankingService {

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
    List<StaPlayerRanking> getPlayersByLeagueId(Long leagueId);


    public HashMap<String,Object> getBestOfGame(Long compeid, Long homeid, Long awayid);

}

