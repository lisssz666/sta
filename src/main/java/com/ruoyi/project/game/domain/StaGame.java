package com.ruoyi.project.game.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.project.game.address.domain.GameAddress;
import jdk.nashorn.internal.ir.annotations.Ignore;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.framework.aspectj.lang.annotation.Excel;
import com.ruoyi.framework.web.domain.BaseEntity;

import java.util.Date;

/**
 * 比赛信息对象 sta_game
 * 
 * @author ruoyi
 * @date 2023-03-28
 */

@Data
public class StaGame extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** id */
    private Long id;

    /** 主队名称 */
    @Excel(name = "主队名称")
    private String homeName;

    /** 客队名称 */
    @Excel(name = "客队名称")
    private String awayName;

    /** 比赛类型 */
    private String gameType;

    /** 主队id */
    @Excel(name = "主队id")
    private Long homeid;

    /** 客队id */
    @Excel(name = "客队id")
    private Long awayid;

    /** 主队分数 */
    private Integer hteamScore;

    /** 客队分数 */
    private Integer vteamScore;

    /** 联赛id */
    private Long leagueId;

    /** 所属联赛名称 */
    private String leagueName;

    /** 联赛logo */
    private String leagueLogoPath;

    /** 比赛时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String playingTime;

    /** 比赛轮次 */
    private String gameRound;

    /** 比赛场地id */
    private Long gameAddrId;

    /** 比赛球馆名称 */
    private String venueName;

    /** 比赛场地地区 */
    private String location;

    /** 比赛详细地址 */
    private String gameAddr;

    /** 比赛状态 0未开始，1进行中，2已结束*/
    private Integer gameStatus;

    /** 比赛阶段 */
    private String gameStage;

    /** 主队球衣颜色 */
    private String homeColor;

    /** 客队球衣颜色 */
    private String awayColor;

    /** 主队暂停次数 */
    private int homePaused;

    /** 客队暂停次数 */
    private int awayPaused;

    /** 主队犯规次数 */
    private int homeFouls;

    /** 客队犯规次数 */
    private int awayFouls;

    /** 主队队徽 */
    private String homeTeamLogo;

    /** 客队队徽 */
    private String awayTeamLogo;

    /** 主队每节得分情况 */
    private String hsessionsScore;

    /** 客队每节得分情况 */
    private String asessionsScore;


    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setHomeName(String homeName) 
    {
        this.homeName = homeName;
    }

    public String getHomeName() 
    {
        return homeName;
    }
    public void setAwayName(String awayName) 
    {
        this.awayName = awayName;
    }

    public String getAwayName() 
    {
        return awayName;
    }
    public void setHomeid(Long homeid) 
    {
        this.homeid = homeid;
    }

    public Long getHomeid() 
    {
        return homeid;
    }
    public void setAwayid(Long awayid) 
    {
        this.awayid = awayid;
    }

    public Long getAwayid() 
    {
        return awayid;
    }

}
