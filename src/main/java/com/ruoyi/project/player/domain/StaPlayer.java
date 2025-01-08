package com.ruoyi.project.player.domain;

import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.framework.aspectj.lang.annotation.Excel;
import com.ruoyi.framework.web.domain.BaseEntity;
import org.springframework.web.multipart.MultipartFile;

/**
 * 运动员信息对象 sta_player
 * 
 * @author ruoyi
 * @date 2023-03-28
 */
@Data
public class StaPlayer extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** id */
    private Long id;

    /** 所属球队id */
    private Long teamId;

    /** 球员logo */
    private MultipartFile logo;

    /** 球员logo路径 */
    private String logoPath;

    /** 球员姓名 */
    private String name;

    /** 球衣号码 */
    private Integer jerseyNumber;

    /** 体重（kg） */
    private Double weight;

    /** 身高（cm） */
    private Double height;

    /** 位置 */
    private String position;

    /** 手机号 */
    private String phoneNumber;

}
