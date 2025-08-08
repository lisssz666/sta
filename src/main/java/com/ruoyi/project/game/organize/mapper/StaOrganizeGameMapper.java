package com.ruoyi.project.game.organize.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.project.game.organize.domain.StaOrganizeGameEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

@Mapper
public interface StaOrganizeGameMapper extends BaseMapper<StaOrganizeGameEntity> {
    List<StaOrganizeGameEntity> selectAllList(StaOrganizeGameEntity entity);
    int insert(StaOrganizeGameEntity entity);
    int deleteById(Long id);
}
