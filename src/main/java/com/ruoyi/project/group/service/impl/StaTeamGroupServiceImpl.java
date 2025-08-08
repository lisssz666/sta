package com.ruoyi.project.group.service.impl;

import com.ruoyi.project.group.domain.StaTeamGroup;
import com.ruoyi.project.group.domain.dto.AddTeamsRequest;
import com.ruoyi.project.group.mapper.StaTeamGroupMapper;
import com.ruoyi.project.group.service.StaTeamGroupService;
import com.ruoyi.project.team.domain.StaTeam;
import com.ruoyi.project.team.mapper.StaTeamMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 球队分组服务实现类
 */
@Service
public class StaTeamGroupServiceImpl implements StaTeamGroupService {

    @Autowired
    private StaTeamGroupMapper teamGroupMapper;

    @Autowired
    private StaTeamMapper staTeamMapper;

    //服务器
    @Value("${spring.upload.server}")
    private String server;


    @Override
    public int insertTeamGroup(StaTeamGroup teamGroup) {
        return teamGroupMapper.insertTeamGroup(teamGroup);
    }

    @Override
    public int updateTeamGroup(StaTeamGroup teamGroup) {
        return teamGroupMapper.updateTeamGroup(teamGroup);
    }

    @Override
    public int deleteTeamGroup(Long id) {
        return teamGroupMapper.deleteTeamGroup(id);
    }

    @Override
    public List<StaTeamGroup> selectTeamGroupsByLeagueId(Long leagueId) {
        // 从数据库中根据联赛ID获取所有分组信息
        List<StaTeamGroup> staTeamGroups = teamGroupMapper.selectTeamGroupsByLeagueId(leagueId);
        // 遍历每一个团队组
        staTeamGroups.forEach(group -> {
            // 获取当前团队组中的团队ID字符串（以逗号分隔）
            String teamIds = group.getTeamIds();

            // 判断teamIds不为空
            if (teamIds != null && !teamIds.trim().isEmpty()) {
                // 将团队ID字符串分割成数组，并转换为流
                List<StaTeam> teams = Arrays.stream(teamIds.split(","))
                        // 将每个字符串转换为Long类型的团队ID
                        .map(Long::valueOf)
                        // 根据团队ID从数据库中查询对应的团队信息
                        .map(staTeamMapper::selectStaTeamById)
                        // 过滤掉为null的团队信息
                        .filter(Objects::nonNull)
                        // 修改teamLogoPath字段，在前面加上server
                        .map(team -> {
                            if (team.getTeamLogoPath() != null && !team.getTeamLogoPath().isEmpty()) {
                                team.setTeamLogoPath(server + team.getTeamLogoPath());
                            }if (team.getTeamPhoto() != null && !team.getTeamPhoto().isEmpty()) {
                                team.setTeamPhotoPath(server + team.getTeamPhotoPath());
                            }
                            return team;
                        })
                        // 收集结果到一个新的列表中
                        .collect(Collectors.toList());

                // 将查询到的团队信息设置到当前团队组中
                group.setTeamInfo(teams);
            }
        });
        // 返回包含完整团队信息的团队组列表
        return staTeamGroups;
    }

    @Override
    public int addTeamsToGroup(AddTeamsRequest request) {
        Long groupId = request.getGroupId();
        List<Long> teamIds = request.getTeamIds();
        String teamIdsStr = String.join(",", teamIds.stream().map(String::valueOf).collect(Collectors.toList()));
        return teamGroupMapper.addTeamsToGroup(groupId, teamIdsStr);
    }

    @Override
    public int removeTeamsFromGroup(AddTeamsRequest request) {

        return teamGroupMapper.removeTeamsFromGroup(request.getGroupId(), request.getTeamId());
    }

}

