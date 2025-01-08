package com.ruoyi.project.sponsor.mapper;


import com.ruoyi.project.sponsor.domain.StaSponsor;

import java.util.List;

/**
 * 球队信息Mapper接口
 * 
 * @author ruoyi
 * @date 2023-03-28
 */
public interface StaSponsorMapper
{

    // 查询所有赞助商信息
    List<StaSponsor> selectAllSponsors();

    // 根据ID查询赞助商信息
    StaSponsor selectSponsorById(Long id);

    // 插入新的赞助商信息
    int insertSponsor(StaSponsor sponsor);

    // 根据ID更新赞助商信息
    int updateSponsorById(StaSponsor sponsor);

    // 根据ID删除赞助商信息
    int deleteSponsorById(Long id);
}
