package com.ruoyi.project.player.mapper;

import java.util.List;
import com.ruoyi.project.player.domain.StaPlayer;

/**
 * 运动员信息Mapper接口
 * 
 * @author ruoyi
 * @date 2023-03-28
 */
public interface StaPlayerMapper 
{
    /**
     * 查询运动员信息
     * 
     * @param id 运动员信息主键
     * @return 运动员信息
     */
    public StaPlayer selectStaPlayerById(Long id);

    /**
     * 查询运动员信息列表
     * 
     * @param staPlayer 运动员信息
     * @return 运动员信息集合
     */
    public List<StaPlayer> selectStaPlayerList(StaPlayer staPlayer);

    /**
     * 新增运动员信息
     * 
     * @param staPlayer 运动员信息
     * @return 结果
     */
    public int insertStaPlayer(StaPlayer staPlayer);

    /**
     * 修改运动员信息
     * 
     * @param staPlayer 运动员信息
     * @return 结果
     */
    public int updateStaPlayer(StaPlayer staPlayer);

    /**
     * 删除运动员信息
     * 
     * @param id 运动员信息主键
     * @return 结果
     */
    public int deleteStaPlayerById(Long id);

    /**
     * 批量删除运动员信息
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteStaPlayerByIds(Long[] ids);
}
