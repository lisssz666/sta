package com.ruoyi.project.rank.controller;

import com.ruoyi.framework.web.controller.BaseController;
import com.ruoyi.framework.web.domain.AjaxResult;
import com.ruoyi.project.rank.domain.StaPlayerRanking;
import com.ruoyi.project.rank.service.IStaTeamRankingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

/**
 * 球队榜Controller
 */
@RestController
@RequestMapping("/rank/teamRanking")
public class StaTeamRankingController extends BaseController {

    @Autowired
    private IStaTeamRankingService staTeamRankingService;

    /**
     * 根据联赛ID查询球队榜
     *
     * @param leagueId 联赛ID
     * @return 球队榜列表
     */
    @GetMapping("/teamByLeagueId")
    public AjaxResult getTeamRankingsByLeagueId(@RequestParam Long leagueId) {
        return AjaxResult.success(staTeamRankingService.selectByLeagueId(leagueId));
    }

    /**
     * 根据联赛ID查询球员榜
     * @param leagueId 联赛ID
     * @return 球员榜列表
     */
    @GetMapping("/playersByLeagueId")
    public AjaxResult getPlayersByLeagueId(@RequestParam Long leagueId) {
        List<StaPlayerRanking> playerRankings = staTeamRankingService.getPlayersByLeagueId(leagueId);
        return AjaxResult.success(playerRankings);
    }

    @GetMapping("/getBestOfGame")
    public AjaxResult getBestOfGame(Long compeid, Long homeid, Long awayid) {
        HashMap<String,Object> bestOfGame = staTeamRankingService.getBestOfGame(compeid, homeid, awayid);
        return AjaxResult.success(bestOfGame);
    }
}
