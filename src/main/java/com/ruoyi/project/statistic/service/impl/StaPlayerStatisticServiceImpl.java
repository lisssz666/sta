package com.ruoyi.project.statistic.service.impl;

import java.sql.Wrapper;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.framework.web.domain.AjaxResult;
import com.ruoyi.project.game.domain.StaGame;
import com.ruoyi.project.game.mapper.StaGameMapper;
import com.ruoyi.project.game.service.impl.StaGameServiceImpl;
import com.ruoyi.project.match.domain.StaLeagueMatch;
import com.ruoyi.project.match.mapper.StaLeagueMatchMapper;
import com.ruoyi.project.player.domain.StaPlayer;
import com.ruoyi.project.player.mapper.StaPlayerMapper;
import com.ruoyi.project.rank.service.impl.StaTeamRankingServiceImpl;
import com.ruoyi.project.video.minio.config.MinioConfig;
import jdk.nashorn.internal.runtime.regexp.joni.exception.InternalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.project.statistic.mapper.StaPlayerStatisticMapper;
import com.ruoyi.project.statistic.domain.StaPlayerStatistic;
import com.ruoyi.project.statistic.service.IStaPlayerStatisticService;

/**
 * 球员统计Service业务层处理
 * 
 * @author ruoyi
 * @date 2023-03-28
 */
@Service
public class StaPlayerStatisticServiceImpl implements IStaPlayerStatisticService 
{
    private static final Logger log = LoggerFactory.getLogger(StaPlayerStatisticServiceImpl.class);

    @Autowired
    private StaPlayerStatisticMapper staPlayerStatisticMapper;

    @Autowired
    private StaGameMapper staGameMapper;

    @Autowired
    private StaPlayerMapper staPlayerMapper;

    @Autowired
    private StaTeamRankingServiceImpl staTeamRankingServiceImpl;

    /**
     * 查询球员统计
     * 
     * @param id 球员统计主键
     * @return 球员统计
     */
    @Override
    public StaPlayerStatistic selectStaPlayerStatisticById(Long id)
    {
        return staPlayerStatisticMapper.selectStaPlayerStatisticById(id);
    }

    /**
     * 查询球员统计
     *
     * @return 球员统计
     */
    @Override
    public AjaxResult selectStaPlayerStatistic(StaPlayerStatistic staPlayerStatistic)
    {
        Long aLong = null;
            aLong = Optional.ofNullable(staPlayerStatistic)
                    .map(StaPlayerStatistic::getId).orElseGet(() ->{throw new InternalException("传人id不能为空");});

        StaPlayerStatistic staPlayerStatistic2 = staPlayerStatisticMapper.selectById(aLong);

        System.out.println(staPlayerStatistic2);
        StaPlayerStatistic staPlayerStatistic3 = Optional.ofNullable(staPlayerStatistic2)
                .orElseGet(() -> new StaPlayerStatistic());
        System.out.println(staPlayerStatistic3);

        LambdaQueryWrapper<StaPlayerStatistic> lws = new LambdaQueryWrapper<StaPlayerStatistic>()
                .eq(StaPlayerStatistic::getCompeid,1L);
        List<StaPlayerStatistic> staPlayerStatistics = staPlayerStatisticMapper.selectList(lws);


//        StaPlayerStatistic playerStatistic = StaPlayerStatistic.builder()
//                .assist(22L)
//                .backboard(22L)
//                .blockShot(22L)
//                .compeid(22L)
//                .foul(22)
//                .playerId(1L)
//                .compeid(2L)
//                .build();
//        int insert = staPlayerStatisticMapper.insert(playerStatistic);

        return AjaxResult.success(1);

    }

    /**
     * 查询球员统计列表
     * 
     * @param staPlayerStatistic 球员统计
     * @return 球员统计
     */
    @Override
    public AjaxResult selectStaPlayerStatisticList(StaPlayerStatistic staPlayerStatistic)
    {
        Map<String, Object> result = new HashMap<>();

        Optional<Long> compeidOptional = Optional.ofNullable(staPlayerStatistic)
                .map(StaPlayerStatistic::getCompeid);

        if (compeidOptional.isPresent()) {
            Long compeid = compeidOptional.orElse(0L);
            // 在这里处理 compeid 不为空的情况
            StaGame staGame = staGameMapper.selectStaGameById(compeid);
            Optional.ofNullable(staGame).orElseGet(() -> {
                throw new InternalException("没有比赛信息");
            });
            //主客队每节得分
            String hsessionsScore = staGame.getHsessionsScore();
            String[] hsplit = hsessionsScore != null ? hsessionsScore.split(","): new String[0];
            String asessionsScore = staGame.getAsessionsScore();
            String[] asplit = hsessionsScore != null ? asessionsScore.split(","): new String[0];
            result.put("hsessionsScore", hsplit);
            result.put("asessionsScore", asplit);
            //主客队分数
            Integer hteamScore = staGame.getHteamScore();
            Integer vteamScore = staGame.getVteamScore();
            //暂停数
            result.put("hteamPaused", staGame.getHomePaused());
            result.put("vteamPaused", staGame.getAwayPaused());
            //犯规数
            result.put("hteamFouls", staGame.getHomeFouls());
            result.put("vteamFouls", staGame.getAwayFouls());

            result.put("hteamScore", hteamScore);
            result.put("vteamScore", vteamScore);
            // 获取比赛时间的时间戳（毫秒）
            String playingTime = staGame.getPlayingTime();
            if (playingTime != null) {
                String formattedPlayingTime = StaGameServiceImpl.formatPlayingTime(playingTime);
                if (formattedPlayingTime != null) {
                    result.put("playingTime", formattedPlayingTime);
                }
            }
            Long homeid = staGame.getHomeid();
            Long awayid = staGame.getAwayid();
            staPlayerStatistic.setTeamId(homeid);
            staPlayerStatistic.setCompeid(compeid);
            List<StaPlayerStatistic> homeStats = staPlayerStatisticMapper.selectStaPlayerStatisticList(compeid, homeid,null);
            log.info("homeStats : "+homeStats);
            result.put("homeStats", homeStats);
            result.put("homeName", staGame.getHomeName());
            result.put("awayid", awayid);

            staPlayerStatistic.setTeamId(awayid);
            staPlayerStatistic.setCompeid(compeid);
            List<StaPlayerStatistic> awayStats = staPlayerStatisticMapper.selectStaPlayerStatisticList(compeid, null,awayid);
            log.info("awayStats : "+awayStats);
            result.put("awayStats", awayStats);
            result.put("awayName", staGame.getAwayName());
            result.put("homeid", homeid);
            //获取本场主客队最佳得分，篮板，助攻
            if (compeid !=null && homeid !=null && awayid !=null ) {
                HashMap<String, Object> bestOfGame = staTeamRankingServiceImpl.getBestOfGame(compeid, homeid, awayid);
                result.put("bestOfGameHome", bestOfGame);
            }
        } else {
            // 在这里处理 compeid 为空的情况
            return AjaxResult.error("参数不能为空！");
        }
        return AjaxResult.success(result);
    }



    /**
     * 新增球员统计
     * 
     * @param staPlayerStatistic 球员统计
     * @return 结果
     */
    @Override
    public int insertStaPlayerStatistic(StaPlayerStatistic staPlayerStatistic)
    {
//        staPlayerStatistic.setCreateTime(DateUtils.getNowDate());
        return staPlayerStatisticMapper.insertStaPlayerStatistic(staPlayerStatistic);
    }

    /**
     * 修改球员统计
     * 
     * @param staPlayerStatistic 球员统计
     * @return 结果
     */
    @Override
    public AjaxResult updateStaPlayerStatistic(StaPlayerStatistic staPlayerStatistic)
    {
        staPlayerStatistic.setUpdateTime(DateUtils.getNowDate());
        Long compeid1 = staPlayerStatistic.getCompeid();
        Long playerId = staPlayerStatistic.getPlayerId();
        Long teamId = staPlayerStatistic.getTeamId();
        Long homeId = staPlayerStatistic.getHomeid();
        Long awayId = staPlayerStatistic.getAwayid();
        Integer paused1 = staPlayerStatistic.getPaused();
        Optional.ofNullable(compeid1).orElseGet(() -> { throw new InternalException("比赛id不能为空"); });
        if(homeId == null && awayId ==null){
            throw new InternalException("主队id或者客队id不能为空");
        }
        //更新数据库对象
        StaPlayerStatistic playerStatistic1 = new StaPlayerStatistic();
        StaPlayerStatistic playerStatistic = new StaPlayerStatistic();
        //暂停不需要传playerId，也不需要统计球员表
        if (paused1 == null) {
            Optional.ofNullable(playerId).orElseGet(() -> {
                throw new InternalException("球员id不能为空");
            });

            if (homeId != null){
                teamId =homeId;
            }else if (awayId != null){
                teamId =awayId;
            }
            //通过比赛id，球员id，球队id 来修改统计列表
            playerStatistic1.setCompeid(compeid1);
            playerStatistic1.setPlayerId(playerId);
            playerStatistic1.setTeamId(teamId);
            //查询有没有这条数据统计
            playerStatistic = staPlayerStatisticMapper.selStatisticOnlyOne(compeid1,playerId,teamId);
            if (playerStatistic == null) {
                log.info("compeid " +compeid1,"    playerId " +playerId,"   teamId " +teamId );
                //没有本比赛球员统计，第一次统计，先创建
                StaPlayerStatistic initialStatistic = new StaPlayerStatistic();
                String playerName = staPlayerStatistic.getNameNum() !=null ? staPlayerStatistic.getNameNum() :null;
                Integer jerseyNumber = staPlayerStatistic.getJerseyNumber() !=null ? staPlayerStatistic.getJerseyNumber() :null;
                initialStatistic.setPlayerId(playerId);
                initialStatistic.setCompeid(compeid1);
                Long teamId1 = staPlayerStatistic.getHomeid() != null ? staPlayerStatistic.getHomeid() : staPlayerStatistic.getAwayid();
                initialStatistic.setTeamId(teamId1);
                initialStatistic.setNameNum(playerName);
                initialStatistic.setJerseyNumber(jerseyNumber);
                int i = staPlayerStatisticMapper.insertStaPlayerStatistic(initialStatistic);
                log.info("新建比赛统计完成 " + i);
                playerStatistic= new StaPlayerStatistic();
            }
        }

        playerStatistic1.setId(staPlayerStatistic.getId());

        //得分
        if (staPlayerStatistic.getScore() != null) {
            playerStatistic1.setScore((playerStatistic.getScore() != null ? playerStatistic.getScore() : 0) + staPlayerStatistic.getScore());
        }
        //篮板
        if (staPlayerStatistic.getBackboard() != null) {
            long l = (playerStatistic.getBackboard() != null ? playerStatistic.getBackboard() : 0) + staPlayerStatistic.getBackboard();
            if (l<0){
                playerStatistic1.setBackboard(0L);
            }else {
                playerStatistic1.setBackboard((playerStatistic.getBackboard() != null ? playerStatistic.getBackboard() : 0) + staPlayerStatistic.getBackboard());
            }
        }

        //助攻
        if (staPlayerStatistic.getAssist() != null) {
            long l = (playerStatistic.getAssist() != null ? playerStatistic.getAssist() : 0) + staPlayerStatistic.getAssist();
            if (l<0){
                playerStatistic1.setAssist(0L);
            }else {
                playerStatistic1.setAssist((playerStatistic.getAssist() != null ? playerStatistic.getAssist() : 0) + staPlayerStatistic.getAssist());
            }
        }

        //抢断
        if (staPlayerStatistic.getTackle() != null) {
            long l = (playerStatistic.getTackle() != null ? playerStatistic.getTackle() : 0) + staPlayerStatistic.getTackle();
            if (l<0){
                playerStatistic1.setTackle(0L);
            }else {
                playerStatistic1.setTackle((playerStatistic.getTackle() != null ? playerStatistic.getTackle() : 0) + staPlayerStatistic.getTackle());
            }
        }

        //盖帽
        if (staPlayerStatistic.getCover() != null) {
            long l = (playerStatistic.getCover() != null ? playerStatistic.getCover() : 0) + staPlayerStatistic.getCover();
            if (l<0){
                playerStatistic1.setCover(0L);
            }else {
                playerStatistic1.setCover((playerStatistic.getCover() != null ? playerStatistic.getCover() : 0) + staPlayerStatistic.getCover());
            }
        }

        //犯规
        if (staPlayerStatistic.getFoul() != null) {
            long l = (playerStatistic.getFoul() != null ? playerStatistic.getFoul() : 0) + staPlayerStatistic.getFoul();
            if (l<0){
                playerStatistic1.setFoul(0);
            }else {
                playerStatistic1.setFoul((playerStatistic.getFoul() != null ? playerStatistic.getFoul() : 0) + staPlayerStatistic.getFoul());
            }
            //修改比赛总犯规
            StaGame staGame = new StaGame();
            staGame.setId(compeid1);
            if (homeId != null) {
                staGame.setHomeFouls(staPlayerStatistic.getFoul());
            }else if (awayId != null){
                staGame.setAwayFouls(staPlayerStatistic.getFoul());
            }
            staGameMapper.updateScoreById(staGame);
        }

        //投篮
        if (staPlayerStatistic.getShoot() != null) {
            playerStatistic1.setShoot((playerStatistic.getShoot() != null ? playerStatistic.getShoot() : 0) + staPlayerStatistic.getShoot());
        }

        //二分球
            String shootTheBall1 = staPlayerStatistic.getShootTheBall();
            if (shootTheBall1 != null) {
                String shootTheBall = playerStatistic.getShootTheBall() != null ? playerStatistic.getShootTheBall(): null;
                String shoot = playerStatistic.getShoot() != null ? playerStatistic.getShoot() : null;
                if ("2".equals(shootTheBall1) || "-2".equals(shootTheBall1)){
                    int towScore = Integer.parseInt(shootTheBall1);
                    //得分+2
                    Long score = playerStatistic.getScore();
                    if ("-2".equals(shootTheBall1 ) && score ==0){
                        playerStatistic1.setScore(score);
                    }else {
                        playerStatistic1.setScore((score !=null ? score : 0) + towScore);
                    }
                    //总分计算
                    Long compeid = staPlayerStatistic.getCompeid();
                    Long homeid = staPlayerStatistic.getHomeid();
                    Long awayid = staPlayerStatistic.getAwayid();
                    if (compeid ==null ){
                        return AjaxResult.error(0,"参数compeid不能为null");
                    }
//                    StaGame staGame1 = staGameMapper.selectStaGameById(compeid);
                    Integer hteamScore = 0;
                    Integer vteamScore = 0;
                    StaGame staGame = new StaGame();

                    if (homeid != null){
                        hteamScore =towScore;
                        staGame.setHomeid(homeid);
                    }if (awayid != null){
                        vteamScore =towScore;
                        staGame.setAwayid(awayid);
                    }
                    //修改比赛总比分
                    staGame.setId(compeid);
                    staGame.setHteamScore(hteamScore);
                    staGame.setVteamScore(vteamScore);
                    staGameMapper.updateScoreById(staGame);
                }
                //投篮+2
                String shoot1 =null;
                if (StringUtils.isNotEmpty(shoot)) {
                    String[] parts = shoot.split("/");
                    int numeratort = Integer.parseInt(parts[0]);
                    int denominatort = Integer.parseInt(parts[1]);
                    //-2是撤回
                    if ("2".equals(shootTheBall1) || "-2".equals(shootTheBall1)) {
                        if ("-2".equals(shootTheBall1)){
                            //  0/0就不撤回意义
                            if(numeratort != 0 && denominatort != 0){
                                int num1 = numeratort - 1;
                                int den1 = denominatort - 1;
                                shoot1 = num1 + "/" + den1;
                            }
                        }else {
                            int num1 = numeratort + 1;
                            int den1 = denominatort + 1;
                            shoot1 = num1 + "/" + den1;
                        }
                        //-4是撤回投篮不进
                    }else if("-4".equals(shootTheBall1)){
                        if(denominatort != 0) {
                            int den1 = denominatort - 1;
                            shoot1 = numeratort + "/" + den1;
                        }
                    }else {
                        int den1 = denominatort + 1;
                        shoot1 = numeratort + "/" + den1;
                    }
                }else{
                    if ("2".equals(shootTheBall1)) {
                        shoot1 = 1 + "/" + 1;
                    }else {
                        shoot1 = 0 + "/" + 1;
                    }
                }
                playerStatistic1.setShoot(shoot1);
                //统计2
                String res =null;
                if (StringUtils.isNotEmpty(shootTheBall)) {
                    String[] parts = shootTheBall.split("/");
                    int numerator = Integer.parseInt(parts[0]);
                    int denominator = Integer.parseInt(parts[1]);
                    if ("2".equals(shootTheBall1) || "-2".equals(shootTheBall1)) {
                        if ("-2".equals(shootTheBall1)){
                            if(numerator != 0 && denominator != 0){
                                int num1 = numerator - 1;
                                int den1 = denominator - 1;
                                res = num1 + "/" + den1;
                            }
                        }else {
                            int num1 = numerator + 1;
                            int den1 = denominator + 1;
                            res = num1 + "/" + den1;
                        }
                    //-4是撤回投篮不进
                    }else if("-4".equals(shootTheBall1)){
                        if(denominator != 0) {
                            int den1 = denominator - 1;
                            res = numerator + "/" + den1;
                        }
                    }else {
                        int den1 = denominator + 1;
                        res = numerator + "/" + den1;
                    }
                }else{
                    if ("2".equals(shootTheBall1)) {
                        res = 1 + "/" + 1;
                    }else {
                        res = 0 + "/" + 1;
                    }
                }
                playerStatistic1.setShootTheBall(res);

            }
            //三分球
            String trisection = staPlayerStatistic.getTrisection();
            if (trisection != null) {
                String trisection1 = playerStatistic.getTrisection() !=null ? playerStatistic.getTrisection() :null;
                String shoot3 = playerStatistic.getShoot() !=null ? playerStatistic.getShoot() : null;
                //-3是撤回
                if ("3".equals(trisection) || "-3".equals(trisection)){
                    int thrScore = Integer.parseInt(trisection);
                    //得分+3
                    Long score = playerStatistic.getScore() != null ? playerStatistic.getScore() : null;
                    if ("-3".equals(trisection ) && score ==0) {
                        playerStatistic1.setScore(score);
                    }else {
                        playerStatistic1.setScore((score !=null ? score:0) + thrScore);
                    }
                    //总分计算
                    Long compeid = staPlayerStatistic.getCompeid();
                    Long homeid = staPlayerStatistic.getHomeid();
                    Long awayid = staPlayerStatistic.getAwayid();
                    if (compeid ==null ){
                        return AjaxResult.error(0,"参数compeid不能为null");
                    }
//                    StaGame staGame1 = staGameMapper.selectStaGameById(compeid);
                    Integer hteamScore = 0;
                    Integer vteamScore = 0;
                    StaGame staGame = new StaGame();

                    if (homeid != null){
                        hteamScore =thrScore;
                        staGame.setHomeid(homeid);
                    }if (awayid != null){
                        vteamScore =thrScore;
                        staGame.setAwayid(awayid);
                    }
                    staGame.setId(compeid);
                    staGame.setHteamScore(hteamScore);
                    staGame.setVteamScore(vteamScore);
                    staGameMapper.updateScoreById(staGame);
                }
                //投篮+3
                String shoot1 =null;
                if (StringUtils.isNotEmpty(shoot3)) {
                    String[] parts = shoot3.split("/");
                    int numerator = Integer.parseInt(parts[0]);
                    int denominator = Integer.parseInt(parts[1]);
                    if ("3".equals(trisection) || "-3".equals(trisection)) {
                        if ("-3".equals(trisection)){
                            if(numerator != 0 && denominator != 0) {
                                int num1 = numerator - 1;
                                int den1 = denominator - 1;
                                shoot1 = num1 + "/" + den1;
                            }
                        }else {
                            int num1 = numerator + 1;
                            int den1 = denominator + 1;
                            shoot1 = num1 + "/" + den1;
                        }
                    }else if("-4".equals(trisection)){
                        if (denominator != 0) {
                            int den1 = denominator - 1;
                            shoot1 = numerator + "/" + den1;
                        }
                    }else {
                        int den1 = denominator + 1;
                        shoot1 = numerator + "/" + den1;
                    }
                }else{
                    if ("3".equals(trisection)) {
                        shoot1 = 1 + "/" + 1;
                    }else {
                        shoot1 = 0 + "/" + 1;
                    }
                }
                playerStatistic1.setShoot(shoot1);
                //三分+3
                String res =null;
                if (StringUtils.isNotEmpty(trisection1)) {
                    String[] parts = trisection1.split("/");
                    int numerator = Integer.parseInt(parts[0]);
                    int denominator = Integer.parseInt(parts[1]);
                    if ("3".equals(trisection)|| "-3".equals(trisection)) {
                        if ("-3".equals(trisection)){
                            if (denominator != 0 && numerator != 0) {
                                int num1 = numerator - 1;
                                int den1 = denominator - 1;
                                res = num1 + "/" + den1;
                            }
                        }else {
                            int num1 = numerator + 1;
                            int den1 = denominator + 1;
                            res = num1 + "/" + den1;
                        }
                    }else if("-4".equals(trisection)){
                        if (denominator != 0) {
                            int den1 = denominator - 1;
                            res = numerator + "/" + den1;
                        }
                    }else {
                        int den1 = denominator + 1;
                        res = numerator + "/" + den1;
                    }
                }else{
                    if ("3".equals(trisection)) {
                        res = 1 + "/" + 1;
                    }else {
                        res = 0 + "/" + 1;
                    }
                }
                playerStatistic1.setTrisection(res);
            }
            //罚球
            String freeThrow = staPlayerStatistic.getFreeThrow();
            if (freeThrow != null) {
                String freeThrow1 = playerStatistic.getFreeThrow() !=null ? playerStatistic.getFreeThrow() :null;
                //-1是撤回
                if ("1".equals(freeThrow) || "-1".equals(freeThrow)){
                    int freeScore = Integer.parseInt(freeThrow);
                    //得分+1
                    Long score = playerStatistic.getScore() != null ? playerStatistic.getScore() : null;
                    if ("-1".equals(freeThrow) && score == 0){
                        playerStatistic1.setScore(score);
                    }else {
                        playerStatistic1.setScore((score !=null ? score:0) + freeScore);
                    }
                    //总分计算
                    Long compeid = staPlayerStatistic.getCompeid();
                    Long homeid = staPlayerStatistic.getHomeid();
                    Long awayid = staPlayerStatistic.getAwayid();
                    if (compeid ==null ){
                        return AjaxResult.error(0,"参数compeid不能为null");
                    }
//                    StaGame staGame1 = staGameMapper.selectStaGameById(compeid);
                    Integer hteamScore = 0;
                    Integer vteamScore = 0;
                    StaGame staGame = new StaGame();

                    if (homeid != null){
                        hteamScore =freeScore;
                        staGame.setHomeid(homeid);
                    }if (awayid != null){
                        vteamScore =freeScore;
                        staGame.setHomeid(homeid);
                    }
                    staGame.setId(compeid);
                    staGame.setHteamScore(hteamScore);
                    staGame.setVteamScore(vteamScore);
                    staGameMapper.updateScoreById(staGame);
                }
                String res =null;
                if (StringUtils.isNotEmpty(freeThrow1)) {
                    String[] parts = freeThrow1.split("/");
                    int numerator = Integer.parseInt(parts[0]);
                    int denominator = Integer.parseInt(parts[1]);
                    if ("1".equals(freeThrow)|| "-1".equals(freeThrow)) {
                        if ("-1".equals(freeThrow)){
                            if (denominator != 0 && numerator != 0) {
                                int num1 = numerator - 1;
                                int den1 = denominator - 1;
                                res = num1 + "/" + den1;
                            }
                        }else {
                            int num1 = numerator + 1;
                            int den1 = denominator + 1;
                            res = num1 + "/" + den1;
                        }

                    }else if("-4".equals(freeThrow)){
                        if (denominator != 0){
                            int den1 = denominator - 1;
                            res = numerator + "/" + den1;
                        }
                    }else {
                        int den1 = denominator + 1;
                        res = numerator + "/" + den1;
                    }
                }else{
                    if ("1".equals(freeThrow)) {
                        res = 1 + "/" + 1;
                    }else {
                        res = 0 + "/" + 1;
                    }
                }
                playerStatistic1.setFreeThrow(res);
            }
            //快攻
            if (staPlayerStatistic.getBlockShot() != null) {
                playerStatistic1.setBlockShot((playerStatistic.getBlockShot() !=null ? playerStatistic.getBlockShot():0) + staPlayerStatistic.getBlockShot());
            }//失误
            if (staPlayerStatistic.getMistake() != null) {
                playerStatistic1.setMistake((playerStatistic.getMistake() !=null ? playerStatistic.getMistake(): 0)+ staPlayerStatistic.getMistake());
            }
            //命中率
            String shoot = playerStatistic1.getShoot() != null ? playerStatistic.getShoot() :null;
            if (StringUtils.isNotEmpty(shoot)) {
                String[] parts = shoot.split("/");
                int numerator = Integer.parseInt(parts[0]);
                int denominator = Integer.parseInt(parts[1]);
                // 浮点数除法
                double result = (double) numerator / denominator;
                //指定保留一位小数的格式
//                double roundedNumber = Math.round(result * 10.0) / 10.0;
                // 创建百分比格式化对象，保留一位小数
                DecimalFormat decimalFormat = new DecimalFormat("0%");

                // 格式化百分比
                String formattedPercentage = decimalFormat.format(result);
                playerStatistic1.setHitRate(formattedPercentage);
            }
        //暂停(只修改比赛，与球员统计无关，所以用if)
        if(staPlayerStatistic.getPaused() != null){
            int paused = staPlayerStatistic.getPaused();
            //修改比赛总暂停
            StaGame staGame = new StaGame();
            staGame.setId(compeid1);
            if (homeId != null) {
                staGame.setHomePaused(paused);
            }else if (awayId != null){
                staGame.setAwayPaused(paused);
            }
            staGameMapper.updateScoreById(staGame);
        }else {
            int i = staPlayerStatisticMapper.updateStaPlayerStatistic(playerStatistic1);
        }
        StaGame staGame = staGameMapper.selectStaGameById(compeid1);
        Map<String, Object> result = new HashMap<>();
        if (staGame != null){
            result.put("homePaused",staGame.getHomePaused());
            result.put("awayPaused",staGame.getAwayPaused());
            result.put("homeFouls",staGame.getHomeFouls());
            result.put("awayFouls",staGame.getAwayFouls());
            result.put("hteamScore",staGame.getHteamScore());
            result.put("vteamScore",staGame.getVteamScore());
        }
        return AjaxResult.success("操作成功",result);
    }

    /**
     * 批量删除球员统计
     * 
     * @param ids 需要删除的球员统计主键
     * @return 结果
     */
    @Override
    public int deleteStaPlayerStatisticByIds(Long[] ids)
    {
        return staPlayerStatisticMapper.deleteStaPlayerStatisticByIds(ids);
    }

    /**
     * 删除球员统计信息
     * 
     * @param id 球员统计主键
     * @return 结果
     */
    @Override
    public int deleteStaPlayerStatisticById(Long id)
    {
        return staPlayerStatisticMapper.deleteStaPlayerStatisticById(id);
    }

}
