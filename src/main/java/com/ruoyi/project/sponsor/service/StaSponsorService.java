package com.ruoyi.project.sponsor.service;

import com.ruoyi.project.sponsor.domain.StaSponsor;

import java.util.List;

public interface StaSponsorService {

    // 查询所有赞助商信息
    List<StaSponsor> getAllSponsors();

    // 根据ID查询赞助商信息
    StaSponsor getSponsorById(Long id);

    // 插入新的赞助商信息
    int addSponsor(StaSponsor sponsor);

    // 根据ID更新赞助商信息
    int updateSponsor(StaSponsor sponsor);

    // 根据ID删除赞助商信息
    int deleteSponsor(Long id);
}
