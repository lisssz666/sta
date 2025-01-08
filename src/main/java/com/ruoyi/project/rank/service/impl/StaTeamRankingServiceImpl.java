package com.ruoyi.project.rank.service.impl;

import com.ruoyi.project.rank.domain.StaPlayerRanking;
import com.ruoyi.project.rank.domain.StaTeamRanking;
import com.ruoyi.project.rank.mapper.StaTeamRankingMapper;
import com.ruoyi.project.rank.service.IStaTeamRankingService;
import com.ruoyi.project.statistic.domain.StaBestOfGame;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 球队榜Service实现类
 */
@Service
public class StaTeamRankingServiceImpl implements IStaTeamRankingService {

    @Autowired
    private StaTeamRankingMapper staTeamRankingMapper;

    /**
     * 根据联赛ID查询球队榜
     *
     * @param leagueId 联赛ID
     * @return 球队榜列表
     */
    @Override
    public List<StaTeamRanking> selectByLeagueId(Long leagueId) {
        return staTeamRankingMapper.selectByLeagueId(leagueId);
    }

    @Override
    public List<StaPlayerRanking> getPlayersByLeagueId(Long leagueId) {
        return staTeamRankingMapper.selectPlayersByLeagueId(leagueId);
    }

    /**
     * 根据比赛ID查询球员本场最佳
     */
    public HashMap<String,Object> getBestOfGame(Long compeid,Long homeid,Long awayid){
        // 从数据库中获取最佳球员数据
        List<StaBestOfGame> bestOfGame = staTeamRankingMapper.getBestOfGame(compeid, homeid, awayid);

        // 使用Stream API来优化字符串拼接，避免使用循环和多个字符串拼接操作
        // 主场最佳球员信息
        String hname = bestOfGame.stream()
                .map(best -> best.getHomePlayerName() != null && !best.getHomePlayerName().isEmpty()
                        ? best.getHomePlayerName() : "-")
                .collect(Collectors.joining("/"));
        hname = hname.isEmpty() ? "-/-/-" : hname;

        String hjerseyNumber = bestOfGame.stream()
                .map(best -> best.getHomePlayerNumber() != null && !best.getHomePlayerNumber().isEmpty()
                        ? best.getHomePlayerNumber() : "-")
                .collect(Collectors.joining("/"));
        hjerseyNumber = hjerseyNumber.isEmpty() ? "-/-/-" : hjerseyNumber;

        String hoptimum = bestOfGame.stream()
                .map(best -> best.getHomeValue() != null && !best.getHomeValue().isEmpty()
                        ? best.getHomeValue() : "-")
                .collect(Collectors.joining("/"));
        hoptimum = hoptimum.isEmpty() ? "-/-/-" : hoptimum;

// 客场最佳球员信息
        String aname = bestOfGame.stream()
                .map(best -> best.getAwayPlayerName() != null && !best.getAwayPlayerName().isEmpty()
                        ? best.getAwayPlayerName() : "-")
                .collect(Collectors.joining("/"));
        aname = aname.isEmpty() ? "-/-/-" : aname;

        String ajerseyNumber = bestOfGame.stream()
                .map(best -> best.getAwayPlayerNumber() != null && !best.getAwayPlayerNumber().isEmpty()
                        ? best.getAwayPlayerNumber() : "-")
                .collect(Collectors.joining("/"));
        ajerseyNumber = ajerseyNumber.isEmpty() ? "-/-/-" : ajerseyNumber;

        String aoptimum = bestOfGame.stream()
                .map(best -> best.getAwayValue() != null && !best.getAwayValue().isEmpty()
                        ? best.getAwayValue() : "-")
                .collect(Collectors.joining("/"));
        aoptimum = aoptimum.isEmpty() ? "-/-/-" : aoptimum;

        // 将结果放入HashMap中
        HashMap<String, Object> map = new HashMap<>();
        map.put("name", hname +"/"+ aname);
        map.put("jerseyNumber", hjerseyNumber +"/"+ ajerseyNumber);
        map.put("optimum", hoptimum +"/"+ aoptimum);
        return map;
    }
}
