package com.ruoyi.project.match.domain;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.framework.aspectj.lang.annotation.Excel;
import com.ruoyi.framework.web.domain.BaseEntity;
import org.springframework.web.multipart.MultipartFile;

/**
 * 联赛信息对象 sta_league_match
 * 
 * @author ruoyi
 * @date 2023-04-20
 */

@ApiModel(description = "联赛信息",value = "StaLeagueMatch")
@Data
public class StaLeagueMatch extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** id */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /** 联赛名称 */
    private String leagueName;

    /** 联赛logo */
    private MultipartFile leagueLogo;

    /** 联赛logo路径 */
    private String leagueLogoPath;

    /** 联赛性质 */
    private String leagueNature;

    /** 报名球队id */
    private int enrollTeam;

    /** 主办单位 */
    private String host;

    /** 承办单位 */
    private String organizer;

    /** 协办单位 */
    private String coOrganizer;

    /** 赞助单位 */
    private String sponsor;

    /** 冠名单位 */
    private String titleSponsor;

    /** 描述 */
    private String notes;

    /** 联赛地点 */
    private String location;

    /** 联赛状态 */
    private int leagueStatus;

    /** 赛事联系人 */
    private String contactPerson;

    /** 赛事联系人电话 */
    private String contactPhone;

    /** 赛事背景图 */
    private MultipartFile leagueBgImage;

    /** 赛事背景图路径 */
    private String leagueBgImagePath;

    /** 比赛场地 */
    private String venue;

    /** 报名开始时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date enrollStarttime;

    /** 报名结束时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date enrollEndtime;

    /** 比赛开始时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date gameStarttime;

    /** 比赛结束时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date gameEndtime;
}
