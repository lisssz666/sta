package com.ruoyi.project.group.mapper;

import com.ruoyi.project.group.domain.StaTeamGroup;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 球队分组Mapper接口
 */
@Mapper
public interface StaTeamGroupMapper {

    /**
     * 插入新的球队分组
     *
     * @param teamGroup 球队分组实体
     * @return 插入记录的数量
     */
    int insertTeamGroup(StaTeamGroup teamGroup);

    /**
     * 更新现有的球队分组
     *
     * @param teamGroup 球队分组实体
     * @return 更新记录的数量
     */
    int updateTeamGroup(StaTeamGroup teamGroup);

    /**
     * 删除指定ID的球队分组
     *
     * @param id 球队分组的主键ID
     * @return 删除记录的数量
     */
    int deleteTeamGroup(Long id);

    /**
     * 根据联赛ID查询球队分组列表
     *
     * @param leagueId 联赛ID
     * @return 球队分组列表
     */
    List<StaTeamGroup> selectTeamGroupsByLeagueId(Long leagueId);

    /**
     * 根据ID查询分组信息
     * @param groupId 分组ID
     * @return 分组信息
     */
    StaTeamGroup selectTeamGroupById(Long groupId);
    /**
     * 新增多个球队ID到分组
     * @param groupId 分组ID
     * @param teamIds 球队ID字符串（逗号分隔）
     * @return 更新结果
     */
    int addTeamsToGroup(@Param("groupId") Long groupId, @Param("teamIds") String teamIds);

    /**
     * 从分组中删除多个球队ID
     * @param groupId 分组ID
     * @param teamId 要删除的球队ID
     * @return 更新结果
     */
    int removeTeamsFromGroup(@Param("groupId") Long groupId, @Param("teamId") Long teamId);
}

