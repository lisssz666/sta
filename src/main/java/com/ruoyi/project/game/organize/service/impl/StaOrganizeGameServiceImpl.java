package com.ruoyi.project.game.organize.service.impl;

import com.ruoyi.project.game.organize.domain.StaOrganizeGameEntity;
import com.ruoyi.project.game.organize.mapper.StaOrganizeGameMapper;
import com.ruoyi.project.game.organize.service.IStaOrganizeGameService;
import com.ruoyi.project.team.domain.StaTeam;
import com.ruoyi.project.team.mapper.StaTeamMapper;
import org.springframework.beans.factory.annotation.Autowired;
import javax.annotation.Resource;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class StaOrganizeGameServiceImpl implements IStaOrganizeGameService {

    @Autowired
    private StaOrganizeGameMapper repository;


    @Override
    public List<StaOrganizeGameEntity> list(StaOrganizeGameEntity entity) {
        return repository.selectAllList(entity);
    }

   /* @Override
    public StaOrganizeGameEntity getById(Long id) {
        return repository.findById(id).orElse(null);
    }*/

    @Override
    public boolean save(StaOrganizeGameEntity entity) {
        repository.insert(entity);
        return true; // JPA save 不会返回 false，异常会抛
    }

    /*@Override
    public boolean updateById(StaOrganizeGameEntity entity) {
        if (!repository.existsById(entity.getId())) {
            return false;
        }
        repository.save(entity); // save 兼具 insert/update
        return true;
    }*/

    @Override
    public boolean removeById(Long id) {
        repository.deleteById(id);
        return true;
    }
}