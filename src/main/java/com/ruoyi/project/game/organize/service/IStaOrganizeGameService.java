package com.ruoyi.project.game.organize.service;

import com.ruoyi.project.game.organize.domain.StaOrganizeGameEntity;
import org.springframework.stereotype.Service;

import java.util.List;

public interface IStaOrganizeGameService {

    List<StaOrganizeGameEntity> list(StaOrganizeGameEntity entity);

//    StaOrganizeGameEntity getById(Long id);

    boolean save(StaOrganizeGameEntity entity);

//    boolean updateById(StaOrganizeGameEntity entity);

    boolean removeById(Long id);
}