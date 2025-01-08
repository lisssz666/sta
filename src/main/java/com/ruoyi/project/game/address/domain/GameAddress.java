package com.ruoyi.project.game.address.domain;

import com.ruoyi.framework.web.domain.BaseEntity;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "sta_game_address")
public class GameAddress extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long leagueId;
    private String venueName;
    private String location;
    private String gameAddr;
}
