package com.ruoyi.project.game.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.framework.web.domain.AjaxResult;
import com.ruoyi.project.game.address.domain.GameAddress;
import com.ruoyi.project.game.address.mapper.GameAddressRepository;
import com.ruoyi.project.match.domain.StaLeagueMatch;
import com.ruoyi.project.match.mapper.StaLeagueMatchMapper;
import com.ruoyi.project.player.domain.StaPlayer;
import com.ruoyi.project.player.mapper.StaPlayerMapper;
import com.ruoyi.project.statistic.domain.StaPlayerStatistic;
import com.ruoyi.project.statistic.mapper.StaPlayerStatisticMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.ruoyi.project.game.mapper.StaGameMapper;
import com.ruoyi.project.game.domain.StaGame;
import com.ruoyi.project.game.service.IStaGameService;

/**
 * 比赛信息Service业务层处理
 * 
 * @author ruoyi
 * @date 2023-03-28
 */
@Service
public class StaGameServiceImpl implements IStaGameService 
{

    //服务器
    @Value("${spring.upload.server}")
    private String server;

    @Autowired
    private StaGameMapper staGameMapper;

    @Autowired
    private StaPlayerStatisticMapper staPlayerStatisticMapper;

    @Autowired
    private StaPlayerMapper staPlayerMapper;

    @Autowired
    private StaLeagueMatchMapper staLeagueMatchMapper;

    @Autowired
    private GameAddressRepository gameAddressRepository;

    /**
     * 查询比赛信息
     * 
     * @param id 比赛信息主键
     * @return 比赛信息
     */
    @Override
    public StaGame selectStaGameById(Long id)
    {
        return staGameMapper.selectStaGameById(id);
    }

    /**
     * 查询比赛信息列表
     * 
     * @param staGame 比赛信息
     * @return 比赛信息
     */
    @Override
    public List<StaGame> selectStaGameList(StaGame staGame)
    {
        List<StaGame> staGames = staGameMapper.selectStaGameList(staGame);
        staGames.stream()
                .forEach(game -> {
                    game.setLeagueLogoPath(server + game.getLeagueLogoPath());
                    game.setHomeTeamLogo(server + game.getHomeTeamLogo());
                    game.setAwayTeamLogo(server + game.getAwayTeamLogo());
                    game.setHomeColor(game.getHomeColor());
                    game.setAwayColor(game.getAwayColor());
                    // 根据场地id查找比赛场地，并且放在比赛对象里返回
                    Long gameAddrId = game.getGameAddrId();
                    if (gameAddrId != null) {
                        gameAddressRepository.findById(gameAddrId).ifPresent(gameAddress -> {
                            game.setVenueName(gameAddress.getVenueName());
                            game.setLocation(gameAddress.getLocation());
                            game.setGameAddr(gameAddress.getGameAddr());
                        });
                    }
                    String formattedPlayingTime = formatPlayingTime(game.getPlayingTime());
                    if (formattedPlayingTime != null) {
                        game.setPlayingTime(formattedPlayingTime);
                    }
                });
        return staGames;
    }

    /**
     * 新增比赛信息
     * 
     * @param staGame 比赛信息
     * @return 结果
     */
    @Override
    public AjaxResult insertStaGame(StaGame staGame)
    {
        staGame.setCreateTime(DateUtils.getNowDate());
        //新增比赛
        staGameMapper.insertStaGame(staGame);

        //新增主客队的初始统计数据
        Long homeid = staGame.getHomeid();
        Long awayid = staGame.getAwayid();
        if (homeid ==null && awayid ==null){
            return AjaxResult.error("主队id或者客队id不能为空！");
        }
        //循环两次新增主客队的初始统计数据
        for (int i =0; i <=1; i++){
            if (i==1){
                homeid = awayid;
            }
            StaPlayer selPlayer = new StaPlayer();
            selPlayer.setTeamId(homeid);
            List<StaPlayer> staPlayers = staPlayerMapper.selectStaPlayerList(selPlayer);
//            List<Long> playerId =  staPlayers.stream().map(StaPlayer::getId).collect(Collectors.toList());
            StaPlayerStatistic staPlayerStatistic = new StaPlayerStatistic();
            for (StaPlayer player :staPlayers){
                staPlayerStatistic.setPlayerId(player.getId());
                staPlayerStatistic.setCompeid(staGame.getId());
                staPlayerStatistic.setTeamId(homeid);
                staPlayerStatistic.setNameNum(player.getName());
                staPlayerStatistic.setJerseyNumber(player.getJerseyNumber());
                staPlayerStatisticMapper.insertStaPlayerStatistic(staPlayerStatistic);
            }
            //每个队都加一个"其他"
            staPlayerStatistic.setJerseyNumber(100);
            staPlayerStatistic.setPlayerId(1000L);
            staPlayerStatistic.setNameNum("其他");
            staPlayerStatisticMapper.insertStaPlayerStatistic(staPlayerStatistic);
        }
        return AjaxResult.success("新增比赛成功",staGame.getId());
    }

    /**
     * 修改比赛信息
     * 
     * @param staGame 比赛信息
     * @return 结果
     */
    @Override
    public int updateStaGame(StaGame staGame) {
        staGame.setUpdateTime(DateUtils.getNowDate());
        int i = staGameMapper.updateStaGame(staGame);
        StaGame staGame1 = staGameMapper.selectStaGameById(staGame.getId());
        int gameStatus = staGame.getGameStatus();
        StaGame game = new StaGame();
        game.setId(staGame.getId());
        // 定义一个方法来更新得分
        updateScoresBasedOnGameStatus(staGame1, game, gameStatus);

        int res = staGameMapper.updateStaGame(game);
        return res;
    }





    /**
     * 批量删除比赛信息
     * 
     * @param ids 需要删除的比赛信息主键
     * @return 结果
     */
    @Override
    public int deleteStaGameByIds(Long[] ids)
    {
        return staGameMapper.deleteStaGameByIds(ids);
    }

    /**
     * 删除比赛信息信息
     * 
     * @param id 比赛信息主键
     * @return 结果
     */
    @Override
    public int deleteStaGameById(Long id)
    {
        return staGameMapper.deleteStaGameById(id);
    }


    //统计两队每节得分，入库
    private void updateScoresBasedOnGameStatus(StaGame staGame1, StaGame game, int gameStatus) {
        String hsessionsScore = staGame1.getHsessionsScore();
        String asessionsScore = staGame1.getAsessionsScore();
        //第一节得分
        if (gameStatus == 2) {
            game.setHsessionsScore(hsessionsScore !=null ? staGame1.getHteamScore().toString() : "0");
            game.setAsessionsScore(asessionsScore !=null ? staGame1.getVteamScore().toString() : "0");
            //第二节得分
        }else if (gameStatus == 4){
            if (hsessionsScore != null) {
                Integer hteamScore = staGame1.getHteamScore();
                Integer sscore = Integer.valueOf(hsessionsScore);
                Integer res = hteamScore - sscore;
                game.setHsessionsScore(hsessionsScore+","+res);
            }
            if (asessionsScore != null) {
                Integer ateamScore = staGame1.getVteamScore();
                Integer sscore = Integer.valueOf(asessionsScore);
                Integer res = ateamScore - sscore;
                game.setAsessionsScore(asessionsScore+","+res);
            }
            //第三节得分
        }else if (gameStatus == 6){
            if (hsessionsScore != null && asessionsScore != null){
                List<String> split = Arrays.asList(hsessionsScore.split(","));
                List<String> asplit = Arrays.asList(asessionsScore.split(","));
                Integer one = Integer.valueOf(split.get(0));
                Integer tow = Integer.valueOf(split.get(1));

                Integer aone = Integer.valueOf(asplit.get(0));
                Integer atow = Integer.valueOf(asplit.get(1));

                Integer hteamScore = staGame1.getHteamScore();
                Integer ateamScore = staGame1.getVteamScore();
                // 检查hteamScore是否为null，如果是null，则给它一个默认值，例如0
                if (hteamScore == null && ateamScore == null) {
                    hteamScore = 0;
                    ateamScore = 0;
                }
                // 执行减法操作
                Integer result = hteamScore - one - tow;
                Integer aresult = ateamScore - aone - atow;
                game.setHsessionsScore(hsessionsScore+","+result);
                game.setAsessionsScore(asessionsScore+","+aresult);
            }
            //第四节得分
        }else if (gameStatus == 8){
            if (hsessionsScore != null && asessionsScore != null){
                List<String> split = Arrays.asList(hsessionsScore.split(","));
                List<String> asplit = Arrays.asList(asessionsScore.split(","));
                Integer one = Integer.valueOf(split.get(0));
                Integer tow = Integer.valueOf(split.get(1));
                Integer three = Integer.valueOf(split.get(2));

                Integer aone = Integer.valueOf(asplit.get(0));
                Integer atow = Integer.valueOf(asplit.get(1));
                Integer athree = Integer.valueOf(asplit.get(2));

                Integer hteamScore = staGame1.getHteamScore();
                Integer ateamScore = staGame1.getVteamScore();
                // 检查hteamScore是否为null，如果是null，则给它一个默认值，例如0
                if (hteamScore == null && ateamScore == null) {
                    hteamScore = 0;
                    ateamScore = 0;
                }
                // 执行减法操作
                Integer result = hteamScore - one - tow -three;
                Integer aresult = ateamScore - aone - atow - athree;
                game.setHsessionsScore(hsessionsScore+","+result);
                game.setAsessionsScore(asessionsScore+","+aresult);
            }
        }
    }

    // 封装日期格式化逻辑到单独的方法中
    public static String formatPlayingTime(String playingTime) {
        if (playingTime == null) {
            return null;
        }
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm EE", Locale.getDefault());
        try {
            Date date = inputFormat.parse(playingTime);
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace(); // 或者记录日志，而不是直接打印堆栈跟踪
            return playingTime; // 返回原始字符串或者null，取决于你的业务需求
        }
    }
}
