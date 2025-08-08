package com.ruoyi.project.referee.service;

import com.ruoyi.project.referee.domain.StaRefereeInfoEntity;

import java.io.IOException;
import java.util.List;

/**
 * 裁判信息接口
 */
public interface IStaRefereeInfoService
{
    List<StaRefereeInfoEntity> list(StaRefereeInfoEntity entity);
    StaRefereeInfoEntity getById(Long id);
    boolean save(StaRefereeInfoEntity entity) throws IOException;
    boolean updateById(StaRefereeInfoEntity entity);
    boolean removeById(Long id);
}
