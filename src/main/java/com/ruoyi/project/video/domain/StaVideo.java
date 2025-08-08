package com.ruoyi.project.video.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jdk.nashorn.internal.ir.annotations.Ignore;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.framework.aspectj.lang.annotation.Excel;
import com.ruoyi.framework.web.domain.BaseEntity;

/**
 * 视频录像文件对象 sta_video
 * 
 * @author ruoyi
 * @date 2023-03-28
 */
@TableName("sta_video")
@Data
public class StaVideo extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** id */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 文件名称 */
    private String fileName;

    /** 文件路径 */
    private String filePath;

    /** 联赛id */
    @JsonIgnore
    private String leagueId;

    /** 比赛id */
    @JsonIgnore
    private String gameId;

    /** 球队id */
    @JsonIgnore
    private String teamId;

    /** 文件类型 */
    private int type;

}
