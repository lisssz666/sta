package com.ruoyi.project.statistic.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.project.statistic.domain.StaPlayerStatistic;
import com.ruoyi.project.video.domain.StaVideo;
import org.apache.ibatis.annotations.Param;

/**
 * 球员统计Mapper接口
 * 
 * @author ruoyi
 * @date 2023-03-28
 */
public interface StaPlayerStatisticMapper extends BaseMapper<StaPlayerStatistic>
{
    /**
     * 查询球员统计
     * 
     * @param id 球员统计主键
     * @return 球员统计
     */
    public StaPlayerStatistic selectStaPlayerStatisticById(Long id);

    /**
     * 根据比赛id，球员id，球队id查询球员统计
     *
     * @return 球员统计
     */
    public StaPlayerStatistic selStatisticOnlyOne(Long compeid,Long playerId,Long teamId);

    /**
     * 查询球员统计列表
     * 
     * @param staPlayerStatistic 球员统计
     * @return 球员统计集合
     */
    public List<StaPlayerStatistic> selectStaPlayerStatisticList(Long compeid,Long homeid,Long awayid);

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
    public int updateStaPlayerStatistic(StaPlayerStatistic staPlayerStatistic);

    /**
     * 删除球员统计
     * 
     * @param id 球员统计主键
     * @return 结果
     */
    public int deleteStaPlayerStatisticById(Long id);

    /**
     * 批量删除球员统计
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteStaPlayerStatisticByIds(Long[] ids);

    public List<StaPlayerStatistic> selectStaPlayerStatisticByCompeId(Long compeid);
}
