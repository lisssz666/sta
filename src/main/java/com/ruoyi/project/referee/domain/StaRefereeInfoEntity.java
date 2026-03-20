package com.ruoyi.project.referee.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ruoyi.framework.web.domain.BaseEntity;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Table(name = "sta_referee_info")
public class StaRefereeInfoEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String avatarPath;
    private MultipartFile avatar;
    private String level;
    private String certDate;
    private String regUnit;
    private String signature;
    private String status;
    private String remark;
    private Integer refereeYears;
    private String price ;
    private Integer orderCount;
    @Column(columnDefinition = "text")
    private String refereeResume; // 执裁履历
    private String overallRating; // 综合评价
    private Double praiseRate; // 好评率
    @Column(columnDefinition = "text")
    @JsonIgnore
    private String scheduleLog;
    
    // 用于JSON序列化的字段，不存储到数据库
    @JsonProperty("scheduleLog")
    private transient List<Map<String, Object>> availableTime;
}
