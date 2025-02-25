package com.ruoyi.project.statistic.service.impl;

import com.ruoyi.framework.web.domain.AjaxResult;
import com.ruoyi.project.game.domain.StaGame;
import com.ruoyi.project.game.mapper.StaGameMapper;
import com.ruoyi.project.player.domain.StaPlayer;
import com.ruoyi.project.player.mapper.StaPlayerMapper;
import com.ruoyi.project.statistic.domain.StaPlayerStatistic;
import com.ruoyi.project.statistic.mapper.StaPlayerStatisticMapper;
import com.ruoyi.project.statistic.service.IStaPlayerStatisticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//语音记录球员数据类
@Service
public class TextToSpeechStatsExtractor {

    @Autowired
    private  StaGameMapper staGameMapper;

    @Autowired
    private  StaPlayerMapper staPlayerMapper;

    @Autowired
    private IStaPlayerStatisticService staPlayerStatisticService;

    public String textToSpeechSta(Long compeid) {

        String text = "主队11号三分不中，主队36号抢到篮板，主队36号传给主队11号两分不中。客队25号抢到篮板客队25号两分命中";

        // 正则表达式匹配队伍、球员号码和事件
        Pattern pattern = Pattern.compile("(主队|客队)(\\d+号)(三分命中|两分命中|罚球命中|三分不中|两分不中|罚球不中|抢到篮板|传给|失误|抢断)");
        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            String team = matcher.group(1); // 提取队伍
            int playerNumber = Integer.parseInt(matcher.group(2).replace("号", "")); // 提取球员号码
            String action = matcher.group(3); // 提取动作

            StaPlayerStatistic staPlayerStatistic = new StaPlayerStatistic();
            staPlayerStatistic.setCompeid(compeid); //确认比赛id
            StaGame staGame = staGameMapper.selectStaGameById(compeid);
            Long homeid = staGame.getHomeid();
            Long awayid = staGame.getAwayid();
            //确认球队id
            if ("主队".equals(team)){
                staPlayerStatistic.setTeamId(homeid);
            }else {
                staPlayerStatistic.setTeamId(awayid);
            }
            StaPlayer staPlayer = new StaPlayer();
            staPlayer.setTeamId(staPlayerStatistic.getTeamId());
            staPlayer.setJerseyNumber(playerNumber);
            List<StaPlayer> staPlayers = staPlayerMapper.selectStaPlayerList(staPlayer);
            if (staPlayers != null){
                StaPlayer staPlayer1 = staPlayers.get(0);
                staPlayerStatistic.setPlayerId(staPlayer1.getId()); //确认球员id
            }
            // 根据动作更新球员数据
            switch (action) {
                case "三分命中":
                    staPlayerStatistic.setTrisection("3");
                    break;
                case "两分命中":
                    staPlayerStatistic.setShootTheBall("2");
                    break;
                case "罚球命中":
                    staPlayerStatistic.setFreeThrow("1");
                    break;
                case "三分不中":
                    staPlayerStatistic.setTrisection("0");
                    break;
                case "两分不中":
                    staPlayerStatistic.setShootTheBall("0");
                    break;
                case "罚球不中":
                    staPlayerStatistic.setFreeThrow("0");
                    break;
                case "抢到篮板":
                    staPlayerStatistic.setBackboard(1L);
                    break;
                case "传给":
                    staPlayerStatistic.setAssist(1L);
                    break;
                case "失误":
                    staPlayerStatistic.setMistake(1L);
                    break;
                case "抢断":
                    staPlayerStatistic.setTackle(1L);
                    break;
            }
            System.out.println(team + " " + playerNumber + "号: " +action);
            AjaxResult result = staPlayerStatisticService.updateStaPlayerStatistic(staPlayerStatistic);
        }
        return "录入成功";
    }
}