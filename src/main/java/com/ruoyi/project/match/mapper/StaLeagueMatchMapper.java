package com.ruoyi.project.match.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.project.match.domain.StaLeagueMatch;
import org.apache.ibatis.annotations.Mapper;

/**
 * 联赛信息Mapper接口
 * 
 * @author ruoyi
 * @date 2023-04-20
 */
@Mapper
public interface StaLeagueMatchMapper extends BaseMapper<StaLeagueMatch>
{
    /**
     * 查询联赛信息
     * 
     * @param id 联赛信息主键
     * @return 联赛信息
     */
    public StaLeagueMatch selectStaLeagueMatchById(Long id);

    /**
     * 查询联赛信息列表
     * 
     * @param staLeagueMatch 联赛信息
     * @return 联赛信息集合
     */
    public List<StaLeagueMatch> selectStaLeagueMatchList(StaLeagueMatch staLeagueMatch);

    /**
     * 新增联赛信息
     * 
     * @param staLeagueMatch 联赛信息
     * @return 结果
     */
    public int insertStaLeagueMatch(StaLeagueMatch staLeagueMatch);

    /**
     * 修改联赛信息
     * 
     * @param staLeagueMatch 联赛信息
     * @return 结果
     */
    public int updateStaLeagueMatch(StaLeagueMatch staLeagueMatch);

    /**
     * 删除联赛信息
     * 
     * @param id 联赛信息主键
     * @return 结果
     */
    public int deleteStaLeagueMatchById(Long id);

    /**
     * 批量删除联赛信息
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteStaLeagueMatchByIds(Long[] ids);

    Integer updateAccessCount();

    Integer getAccessCount();
}
