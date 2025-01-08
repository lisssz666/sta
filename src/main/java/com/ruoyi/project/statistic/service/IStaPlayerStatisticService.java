package com.ruoyi.project.statistic.service;

import java.util.List;

import com.ruoyi.framework.web.domain.AjaxResult;
import com.ruoyi.project.statistic.domain.StaPlayerStatistic;

/**
 * 球员统计Service接口
 * 
 * @author ruoyi
 * @date 2023-03-28
 */
public interface IStaPlayerStatisticService 
{
    /**
     * 查询球员统计
     * 
     * @param id 球员统计主键
     * @return 球员统计
     */
    public StaPlayerStatistic selectStaPlayerStatisticById(Long id);

    public AjaxResult selectStaPlayerStatistic(StaPlayerStatistic staPlayerStatistic);

    /**
     * 查询球员统计列表
     * 
     * @param staPlayerStatistic 球员统计
     * @return 球员统计集合
     */
    public AjaxResult selectStaPlayerStatisticList(StaPlayerStatistic staPlayerStatistic) ;

    /**
     * 新增球员统计
     * 
     * @param staPlayerStatistic 球员统计
     * @return 结果
     */
    public int insertStaPlayerStatistic(StaPlayerStatistic staPlayerStatistic);

    /**
     * 修改球员统计
     * 
     * @param staPlayerStatistic 球员统计
     * @return 结果
     */
    public AjaxResult updateStaPlayerStatistic(StaPlayerStatistic staPlayerStatistic);

    /**
     * 批量删除球员统计
     * 
     * @param ids 需要删除的球员统计主键集合
     * @return 结果
     */
    public int deleteStaPlayerStatisticByIds(Long[] ids);

    /**
     * 删除球员统计信息
     * 
     * @param id 球员统计主键
     * @return 结果
     */
    public int deleteStaPlayerStatisticById(Long id);
}
