package com.ruoyi.project.game.organize.service;

import com.ruoyi.project.game.organize.domain.StaOrganizeGameEntity;

import java.util.List;
import java.util.Map;

/**
 * 组织比赛Service接口
 * 支持新的数据结构：前备注、球队数组(JSON)、后备注
 */
public interface IStaOrganizeGameService {
    /**
     * 查询组织比赛列表
     * @param entity 查询条件
     * @return 组织比赛列表
     */
    List<StaOrganizeGameEntity> list(StaOrganizeGameEntity entity);

    /**
     * 根据ID查询组织比赛信息
     * @param id 主键ID
     * @return 组织比赛信息
     */
    StaOrganizeGameEntity getById(Long id);

    /**
     * 保存组织比赛信息
     * @param entity 组织比赛信息
     * @return 是否保存成功
     */
    boolean save(StaOrganizeGameEntity entity);

    boolean updateById(StaOrganizeGameEntity entity);

    /**
     * 根据ID删除组织比赛信息
     * @param id 主键ID
     * @return 是否删除成功
     */
    boolean removeById(Long id);
    
    /**
     * 添加组织比赛
     * @param requestData 请求数据
     * @return 是否添加成功
     */
    boolean addOrganizeGame(Map<String, Object> requestData);
    
    /**
     * 重发组织比赛
     * @param id 主键ID
     * @return 是否重发成功
     */
    boolean repostOrganizeGame(Long id);
    
    /**
     * 更新组织比赛状态
     * @param id 主键ID
     * @param status 新状态
     * @return 是否更新成功
     */
    boolean updateOrganizeGameStatus(Long id, Integer status);
    
    /**
     * 更新组织比赛信息
     * @param id 主键ID
     * @param requestData 请求数据
     * @return 是否更新成功
     */
    boolean updateOrganizeGame(Map<String, Object> requestData);
}