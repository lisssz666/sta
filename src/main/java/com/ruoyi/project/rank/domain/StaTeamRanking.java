package com.ruoyi.project.rank.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.framework.web.domain.BaseEntity;
import lombok.Data;
import java.io.Serializable;

/**
 * 球队榜实体类
 */
@Data
@TableName("sta_team_ranking")
public class StaTeamRanking extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 球队榜ID */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 联赛ID */
    private Long leagueId;

    /** 球队名 */
    private String teamName;

    /** 球队logo */
    private String teamLogo;

    /** 得分 */
    private Integer score;

    /** 失分 */
    private Integer conceded;

    /** 胜场 */
    private Integer wins;

    /** 负场 */
    private Integer losses;

    /** 比赛场数 */
    private Integer gamesPlayed;
}
