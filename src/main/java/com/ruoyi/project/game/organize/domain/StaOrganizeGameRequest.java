package com.ruoyi.project.game.organize.domain;

import lombok.Data;
import java.util.List;

/**
 * 组织比赛请求对象，用于接收包含多个球队的复杂数据结构
 */
@Data
public class StaOrganizeGameRequest {
    private String preRemark; // 前备注
    private List<StaOrganizeGameEntity> teams; // 球队数组
    private String postRemark; // 后备注
}