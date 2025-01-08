package com.ruoyi.project.rank.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 球员榜实体类
 */
@Data
@TableName("sta_player_ranking")
public class StaPlayerRanking {

    /** 自增ID */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 联赛ID */
    private Long leagueId;

    /** 球员名字 */
    private String playerName;

    /** 得分 */
    private Integer score;

    /** 犯规次数 */
    private Integer fouls;

    /** 球员Logo */
    private String playerLogo;

    /** 首发次数 */
    private Integer starterCount;

    /** 比赛场数 */
    private Integer gamesPlayed;
}
