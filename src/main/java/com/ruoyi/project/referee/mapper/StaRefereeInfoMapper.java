package com.ruoyi.project.referee.mapper;

import com.ruoyi.project.referee.domain.StaRefereeInfoEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 登录用户信息Mapper接口
 * 
 * @author ruoyi
 * @date 2021-12-10
 */
@Mapper
public interface StaRefereeInfoMapper
{

    List<StaRefereeInfoEntity> selectAllList(StaRefereeInfoEntity entity);

    StaRefereeInfoEntity selectById(Long id);

    int insert(StaRefereeInfoEntity entity);

    int updateById(StaRefereeInfoEntity entity);

    int deleteById(Long id);
}
