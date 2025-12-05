package com.ruoyi.project.game.organize.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.ruoyi.framework.web.domain.BaseEntity;
import lombok.Data;

import javax.persistence.*;
import java.util.List;
import java.util.Map;

/**
 * 组织比赛实体类 - 重新设计为包含前备注、球队数组和后备注的结构
 */
@Data
@Table(name = "sta_organize_game")
@TableName("sta_organize_game")
public class StaOrganizeGameEntity extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 前备注
     */
    @Column(name = "pre_remark", length = 500)
    private String preRemark;

    /**
     * 球队数组（JSON格式存储）
     * 包含一个或多个球队信息
     */
    @Column(name = "teams", columnDefinition = "JSON")
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<Map<String, Object>> teams;

    /**
     * 后备注
     */
    @Column(name = "post_remark", length = 500)
    private String postRemark;
    
    /**
     * 状态（0-未开始，1-进行中，2-已完成）
     */
    @Column(name = "status")
    private Integer status;
    
// getter/setter 省略
}
