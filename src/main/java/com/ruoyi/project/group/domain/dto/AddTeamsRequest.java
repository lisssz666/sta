package com.ruoyi.project.group.domain.dto;

import lombok.Data;

import java.util.List;

@Data
public class AddTeamsRequest {
    private Long groupId;
    private Long teamId;
    private List<Long> teamIds;
}
