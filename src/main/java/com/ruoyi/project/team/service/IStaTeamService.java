package com.ruoyi.project.team.service;

import java.io.IOException;
import java.util.List;
import com.ruoyi.project.team.domain.StaTeam;

/**
 * 球队信息Service接口
 * 
 * @author ruoyi
 * @date 2023-03-28
 */
public interface IStaTeamService 
{
    /**
     * 查询球队信息
     * 
     * @param id 球队信息主键
     * @return 球队信息
     */
    public StaTeam selectStaTeamById(Long id);

    /**
     * 查询球队信息列表
     * 
     * @param staTeam 球队信息
     * @return 球队信息集合
     */
    public List<StaTeam> selectStaTeamList(StaTeam staTeam);

    /**
     * 新增球队信息
     * 
     * @param staTeam 球队信息
     * @return 结果
     */
    public Long insertStaTeam(StaTeam staTeam) throws IOException;

    /**
     * 修改球队信息
     * 
     * @param staTeam 球队信息
     * @return 结果
     */
    public int updateStaTeam(StaTeam staTeam) throws IOException;

    /**
     * 批量删除球队信息
     * 
     * @param ids 需要删除的球队信息主键集合
     * @return 结果
     */
    public int deleteStaTeamByIds(Long[] ids);

    /**
     * 删除球队信息信息
     * 
     * @param id 球队信息主键
     * @return 结果
     */
    public int deleteStaTeamById(Long id);
}
