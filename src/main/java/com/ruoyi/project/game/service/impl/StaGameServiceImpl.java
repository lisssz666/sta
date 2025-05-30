package com.ruoyi.project.game.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.framework.web.domain.AjaxResult;
import com.ruoyi.project.game.address.mapper.GameAddressRepository;
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
        StaGame staGame1 = staGameMapper.selectStaGameById(staGame.getId());
        int gameStatus = staGame.getGameStatus();
        StaGame game = new StaGame();
        game.setId(staGame.getId());
        // 定义一个方法来更新得分
        updateScoresBasedOnGameStatus(staGame1, game, gameStatus);
        return 1;
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
        //获取主客队每节得分
        String hsessionsScore = staGame1.getHsessionsScore();
        String asessionsScore = staGame1.getAsessionsScore();

        //第一节得分
        if (gameStatus == 2) {
            String score = staGame1.getHteamScore().toString();
            String hsessionsStr = score +","+"0"+","+"0"+","+"0";
            game.setHsessionsScore( hsessionsStr);
            String ascore = staGame1.getVteamScore().toString();
            String asessionsStr = ascore +","+"0"+","+"0"+","+"0";
            game.setAsessionsScore(asessionsStr);
            //第二节得分
        }
        if (hsessionsScore != null && asessionsScore != null) {
            List<String> split = Arrays.asList(hsessionsScore.split(","));
            List<String> asplit = Arrays.asList(asessionsScore.split(","));
            if (gameStatus == 4) {
                Integer one = Integer.valueOf(split.get(0));
                Integer hteamScore = staGame1.getHteamScore();
                Integer res = hteamScore - one; //当前总分 - 第一节分数 ==第二节分数
                game.setHsessionsScore(one + "," + res + "," + "0" + "," + "0");

                Integer aone = Integer.valueOf(asplit.get(0));
                Integer ateamScore = staGame1.getVteamScore();
                Integer ares = ateamScore - aone;
                game.setAsessionsScore(aone + "," + ares + "," + "0" + "," + "0");

                //第三节得分
            } else if (gameStatus == 6) {
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
                Integer three = hteamScore - one - tow; //总分 - 第一节 - 第二节
                Integer athree = ateamScore - aone - atow;
                game.setHsessionsScore(one + "," + tow + "," + three + "," + "0");
                game.setAsessionsScore(aone + "," + atow + "," + athree + "," + "0");

                //第四节得分
            } else if (gameStatus == 8) {
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
                Integer four = hteamScore - one - tow - three;
                Integer afour = ateamScore - aone - atow - athree;
                game.setHsessionsScore(one + "," + tow + "," + three + "," + four);
                game.setAsessionsScore(one + "," + tow + "," + three + "," + afour);
            } else if (gameStatus == 13) { //13表示比赛已经结束
                // 计算每节比赛的总和
                //主队
                int sum = 0;
                for (String str : split) {
                    if (str.trim().matches("\\d+")) {  // 检查是否是纯数字
                        sum += Integer.parseInt(str.trim());
                    }
                }
                List<String> result = new ArrayList<>(split);
                result.add(String.valueOf(sum));
                //客队
                int asum = 0;
                for (String str : asplit) {
                    if (str.trim().matches("\\d+")) {  // 检查是否是纯数字
                        asum += Integer.parseInt(str.trim());
                    }
                }
                List<String> aresult = new ArrayList<>(asplit);
                aresult.add(String.valueOf(asum));
                // 转换成 String，并用逗号连接（去掉 []）
                String resultString = String.join(",", result);
                String aresultString = String.join(",", aresult);

                game.setHsessionsScore(resultString);
                game.setAsessionsScore(aresultString);
            }
        }
        int res = staGameMapper.updateStaGame(game);
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
