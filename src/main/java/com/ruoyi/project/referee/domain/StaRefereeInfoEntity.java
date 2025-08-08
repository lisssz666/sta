package com.ruoyi.project.referee.domain;
import com.ruoyi.framework.web.domain.BaseEntity;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

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
    private String scheduleLog;
}
