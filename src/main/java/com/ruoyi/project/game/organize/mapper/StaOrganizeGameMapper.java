package com.ruoyi.project.game.organize.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.project.game.organize.domain.StaOrganizeGameEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 组织比赛Mapper接口
 * 支持新的数据结构：前备注、球队数组(JSON)、后备注
 */
@Mapper
public interface StaOrganizeGameMapper extends BaseMapper<StaOrganizeGameEntity> {
    /**
     * 查询组织比赛列表
     * @param entity 查询条件
     * @return 组织比赛列表
     */
    List<StaOrganizeGameEntity> selectAllList(StaOrganizeGameEntity entity);
    
    /**
     * 插入组织比赛信息
     * @param entity 组织比赛信息
     * @return 影响行数
     */
    int insert(StaOrganizeGameEntity entity);
    
    /**
     * 根据ID删除组织比赛信息
     * @param id 主键ID
     * @return 影响行数
     */
    int deleteById(Long id);
    
    /**
     * 根据ID查询组织比赛信息
     * @param id 主键ID
     * @return 组织比赛信息
     */
    StaOrganizeGameEntity selectById(Long id);
}
