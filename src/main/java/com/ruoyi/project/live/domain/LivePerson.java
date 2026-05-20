package com.ruoyi.project.live.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 直播人员信息实体类
 * 描述：用于存储直播人员的详细信息，包括个人基本信息、直播资质、收费标准等
 */
@Data
@TableName("sta_live_person")
public class LivePerson {

    /**
     * 主键ID，自增
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 直播人员姓名
     */
    private String name;

    /**
     * 直播人员头像URL
     */
    private String avatarUrl;

    /**
     * 临时上传头像文件，不入库
     */
    @TableField(exist = false)
    private MultipartFile avatarFile;

    /**
     * 直播资质描述（如：资深直播数据员）
     */
    private String qualification;

    /**
     * 亲友价（元/队）
     */
    private BigDecimal friendPrice;

    /**
     * 直播场次总数
     */
    private Integer liveCount;

    /**
     * 直播内容描述（如：全场直播/统计全队数据/个人集锦等）
     */
    private String contentDesc;

    /**
     * 直播人员状态：0-禁用 1-启用
     */
    private Integer status;

    /**
     * 是否隐藏：true-是 false-否
     */
    private Boolean isHide;

    /**
     * 日程日志（JSON格式存储）
     */
    @JsonIgnore
    private String scheduleLog;
    
    /**
     * 用于JSON序列化的可用时间段，不存储到数据库
     */
    @JsonProperty("scheduleLog")
    private transient List<Map<String, Object>> availableTime;
    
    /**
     * 场地信息列表（仅用于返回给前端，不存储到数据库）
     */
    @TableField(exist = false)
    private List<Map<String, String>> pitch;

    /**
     * 逻辑删除：0-未删 1-已删
     */
    @TableLogic(value = "0", delval = "1")
    private Integer deleted;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
