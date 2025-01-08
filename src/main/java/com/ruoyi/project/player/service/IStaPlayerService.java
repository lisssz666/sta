package com.ruoyi.project.player.service;

import java.io.IOException;
import java.util.List;
import com.ruoyi.project.player.domain.StaPlayer;

/**
 * 运动员信息Service接口
 * 
 * @author ruoyi
 * @date 2023-03-28
 */
public interface IStaPlayerService 
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
    public Long insertStaPlayer(StaPlayer staPlayer) throws IOException;

    /**
     * 修改运动员信息
     * 
     * @param staPlayer 运动员信息
     * @return 结果
     */
    public int updateStaPlayer(StaPlayer staPlayer);

    /**
     * 批量删除运动员信息
     * 
     * @param ids 需要删除的运动员信息主键集合
     * @return 结果
     */
    public int deleteStaPlayerByIds(Long[] ids);

    /**
     * 删除运动员信息信息
     * 
     * @param id 运动员信息主键
     * @return 结果
     */
    public int deleteStaPlayerById(Long id);
}
