package com.ruoyi.project.group.service;

import com.ruoyi.project.group.domain.StaTeamGroup;
import com.ruoyi.project.group.domain.dto.AddTeamsRequest;

import java.util.List;

/**
 * 球队分组服务接口
 */
public interface StaTeamGroupService {

    /**
     * 新建球队分组
     */
    int insertTeamGroup(StaTeamGroup teamGroup);

    /**
     * 编辑球队分组
     */
    int updateTeamGroup(StaTeamGroup teamGroup);

    /**
     * 删除球队分组
     */
    int deleteTeamGroup(Long id);

    /**
     * 根据联赛ID查询分组列表
     */
    List<StaTeamGroup> selectTeamGroupsByLeagueId(Long leagueId);

    /**
     * 批量向分组中添加球队ID
     * @return 操作结果
     */
    int addTeamsToGroup(AddTeamsRequest request);

    /**
     * 批量从分组中删除球队ID
     * @return 操作结果
     */
    int removeTeamsFromGroup(AddTeamsRequest request);
}

