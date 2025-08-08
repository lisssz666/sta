package com.ruoyi.project.game.organize.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.framework.web.domain.BaseEntity;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Table(name = "sta_organize_game")
public class StaOrganizeGameEntity extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "team_id")
    private Long teamId;

    @Column(name = "team_name", nullable = false)
    private String teamName;

    @Column(name = "game_date")
    private String gameDate;

    @Column(name = "game_time")
    private String gameTime;

    @Column(name = "uniform")
    private String uniform;

    @Column(name = "fee")
    private String fee;

    @Column(name = "referee")
    private String referee;

    @Column(name = "level")
    private String level;

    @Column(name = "venue")
    private String venue;

    @Column(name = "contact")
    private String contact;

    @Column(name = "remark")
    private String remark;

    // ========= 以下为临时合并字段 =========
    @Transient   // JPA忽略映射
    private String teamTitle;

    @Transient
    private String teamLogoPath;

    @Transient
    private String teamPhotoPath;

    @Transient
    private String leaderName;

    @Transient
    private String leaderPhone;

    @Transient
    private String city;

    @Transient
    private String homeColor;

    @Transient
    private String awayColor;
// getter/setter 省略
}
