package com.ruoyi.project.team.domain;

import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.framework.aspectj.lang.annotation.Excel;
import com.ruoyi.framework.web.domain.BaseEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

/**
 * 球队信息对象 sta_team
 * 
 * @author ruoyi
 * @date 2023-03-28
 */
@Data
public class StaTeam extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** id */
    private Long id;

    /** idStr */
    private String idStr;

    /** 球队名 */
    private String teamTitle;

    /** 球队logo */
    private MultipartFile teamLogo;

    /** 球队logo 路径*/
    private String teamLogoPath;

    /** 球队照片 */
    private MultipartFile teamPhoto;

    /** 球队照片 路径*/
    private String teamPhotoPath;

    /** 领队名字 */
    private String leaderName;

    /** 领队联系电话 */
    private String leaderPhone;

    /** 所属城市 */
    private String city;

    /** 主场球衣颜色 */
    private String homeColor;

    /** 客场球衣颜色 */
    private String awayColor;
}

