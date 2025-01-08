package com.ruoyi.project.game.service;

import java.util.List;

import com.ruoyi.framework.web.domain.AjaxResult;
import com.ruoyi.project.game.domain.StaGame;

/**
 * 比赛信息Service接口
 * 
 * @author ruoyi
 * @date 2023-03-28
 */
public interface IStaGameService 
{
    /**
     * 查询比赛信息
     * 
     * @param id 比赛信息主键
     * @return 比赛信息
     */
    public StaGame selectStaGameById(Long id);

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
    public AjaxResult insertStaGame(StaGame staGame);

    /**
     * 修改比赛信息
     * 
     * @param staGame 比赛信息
     * @return 结果
     */
    public int updateStaGame(StaGame staGame);

    /**
     * 批量删除比赛信息
     * 
     * @param ids 需要删除的比赛信息主键集合
     * @return 结果
     */
    public int deleteStaGameByIds(Long[] ids);

    /**
     * 删除比赛信息信息
     * 
     * @param id 比赛信息主键
     * @return 结果
     */
    public int deleteStaGameById(Long id);
}
