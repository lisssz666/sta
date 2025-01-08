package com.ruoyi.project.sponsor.domain;

import com.ruoyi.framework.web.domain.BaseEntity;
import lombok.Data;

/**
 * 赞助商对象 sta_sponsor
 * 
 * @author ruoyi
 * @date 2024-09-03
 */
@Data
public class StaSponsor extends BaseEntity{

    /** 主键ID */
    private Long id;

    /** 标题 */
    private String title;

    /** 类型 */
    private String type;

    /** 图标路径 */
    private String icon;

    /** 点击效果 */
    private String clickEffect;
}
