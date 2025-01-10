package com.ruoyi.project.rule.enroll.domain;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "sta_enroll")
@Data
public class StaEnroll {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String startDate;
    private String endDate;
    private Integer teams;
    private Integer capacity;
    private String contact;
    private String phone;
    private String email;
    private String instructions;
    private Integer leagueId;

    // Getters and Setters


}
