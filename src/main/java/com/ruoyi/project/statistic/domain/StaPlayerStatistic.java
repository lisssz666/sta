package com.ruoyi.project.statistic.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.framework.aspectj.lang.annotation.Excel;
import com.ruoyi.framework.web.domain.BaseEntity;

/**
 * 球员统计对象 sta_player_statistic
 * 
 * @author ruoyi
 * @date 2023-03-28
 */
@Data
@Builder
@TableName("sta_player_statistic")
public class StaPlayerStatistic extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** id */
    @TableId(value = "id" ,type= IdType.AUTO)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /** 球员id */
    private Long playerId;

    /** 比赛id */
    private Long compeid;

    /** 得分 */
    private Long score;

    /** 篮板 */
    private Long backboard;

    /** 助攻 */
    private Long assist;

    /** 抢断 */
    private Long tackle;

    /** 盖帽 */
    private Long cover;

    /** 两分球 */
    private String shootTheBall;

    /** 投篮 */
    private String shoot;

    /** 三分球 */
    private String trisection;

    /** 罚球 */
    private String freeThrow;

    /** 快攻 */
    private Long blockShot;

    /** 失误 */
    private Long mistake;

    /** 犯规 */
    private Integer foul;

    /** 暂停 */
    private Integer paused;

    /** 命中率 */
    private String hitRate;

    /** 球队id */
    private Long teamId;

    /*球员姓名*/
    private String nameNum;

    /*球员号码*/
    private Integer jerseyNumber;

    /** 主队id */
    private Long homeid;

    /** 客队id */
    private Long awayid;

    /** 主队分数 */
    private Integer hteamScore;

    /** 客队分数 */
    private Integer vteamScore;


    public StaPlayerStatistic(){}

//
//    /** 球队 */
//    private String teamTitle;
    public StaPlayerStatistic(Long id, Long playerId, Long compeid, Long score, Long backboard, Long assist, Long tackle, Long cover, String shootTheBall, String shoot, String trisection, String freeThrow, Long blockShot, Long mistake, Integer foul, int paused, String hitRate, Long teamId, String nameNum, Integer jerseyNumber, Long homeid, Long awayid, Integer hteamScore, Integer vteamScore) {
        this.id = id;
        this.playerId = playerId;
        this.compeid = compeid;
        this.score = score;
        this.backboard = backboard;
        this.assist = assist;
        this.tackle = tackle;
        this.cover = cover;
        this.shootTheBall = shootTheBall;
        this.shoot = shoot;
        this.trisection = trisection;
        this.freeThrow = freeThrow;
        this.blockShot = blockShot;
        this.mistake = mistake;
        this.foul = foul;
        this.paused = paused;
        this.hitRate = hitRate;
        this.teamId = teamId;
        this.nameNum = nameNum;
        this.jerseyNumber = jerseyNumber;
        this.homeid = homeid;
        this.awayid = awayid;
        this.hteamScore = hteamScore;
        this.vteamScore = vteamScore;
    }

    public StaPlayerStatistic(int jerseyNumber, String teamId) {
        super();
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }
    public void setPlayerId(Long playerId)
    {
        this.playerId = playerId;
    }

    public Long getPlayerId()
    {
        return playerId;
    }
    public void setCompeid(Long compeid) 
    {
        this.compeid = compeid;
    }

    public Long getCompeid() 
    {
        return compeid;
    }
    public void setScore(Long score) 
    {
        this.score = score;
    }

    public Long getScore() 
    {
        return score;
    }
    public void setBackboard(Long backboard) 
    {
        this.backboard = backboard;
    }

    public Long getBackboard() 
    {
        return backboard;
    }
    public void setAssist(Long assist) 
    {
        this.assist = assist;
    }

    public Long getAssist() 
    {
        return assist;
    }

    public Long getTackle() 
    {
        return tackle;
    }
    public void setBlockShot(Long blockShot) 
    {
        this.blockShot = blockShot;
    }

    public Long getBlockShot() 
    {
        return blockShot;
    }
    public void setMistake(Long mistake) 
    {
        this.mistake = mistake;
    }

    public Long getMistake() 
    {
        return mistake;
    }
    public void setFoul(Integer foul)
    {
        this.foul = foul;
    }

    public Integer getFoul()
    {
        return foul;
    }

}
