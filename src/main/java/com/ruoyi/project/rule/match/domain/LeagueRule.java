package com.ruoyi.project.rule.match.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.ruoyi.framework.web.domain.BaseEntity;
import lombok.Data;

@TableName("sta_league_rule")
@Data
public class LeagueRule extends BaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 联赛id */
    private Long leagueId;

    /** 人制 */
    private String fmt;

    /** 赛制 */
    private String sys;

    /** 年龄限制 */
    private String ageLim;

    /** 节数 */
    private Integer periods;

    /** 单节时长 */
    private Integer periodDur;

    /** 加时赛单节时长 */
    private Integer otDur;

    /** 排名规则 */
    private String rankRules;

    /** 胜一场得分 */
    private Integer winPts;

    /** 负一场得分 */
    private Integer lossPts;

}
