package com.ruoyi.project.game.mapper;

import java.util.List;
import com.ruoyi.project.game.domain.StaGame;
import com.ruoyi.project.statistic.domain.StaPlayerStatistic;

/**
 * 比赛信息Mapper接口
 * 
 * @author ruoyi
 * @date 2023-03-28
 */
public interface StaGameMapper 
{
    /**
     * 查询比赛信息
     * 
     * @param compeid 比赛信息主键
     * @return 比赛信息
     */
    public StaGame selectStaGameById(Long compeid);


    /**
     * 查询比赛信息列表
     * 
     * @param staGame 比赛信息
     * @return 比赛信息集合
     */
    public List<StaGame> selectStaGameList(StaGame staGame);

    /**
     * 新增比赛信息
     * 
     * @param staGame 比赛信息
     * @return 结果
     */
    public int insertStaGame(StaGame staGame);

    /**
     * 修改比赛信息
     * 
     * @param staGame 比赛信息
     * @return 结果
     */
    public int updateStaGame(StaGame staGame);

    /**
     * 修改比赛总分数
     *
     * @return 结果
     */
    public int updateScoreById(StaGame staGame);

    /**
     * 删除比赛信息
     * 
     * @param id 比赛信息主键
     * @return 结果
     */
    public int deleteStaGameById(Long id);

    /**
     * 批量删除比赛信息
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteStaGameByIds(Long[] ids);
}
