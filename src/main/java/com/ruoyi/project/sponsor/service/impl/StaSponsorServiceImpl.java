package com.ruoyi.project.sponsor.service.impl;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.project.sponsor.domain.StaSponsor;
import com.ruoyi.project.sponsor.mapper.StaSponsorMapper;
import com.ruoyi.project.sponsor.service.StaSponsorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class StaSponsorServiceImpl implements StaSponsorService
{

    @Autowired
    private StaSponsorMapper sponsorMapper;

    @Override
    public List<StaSponsor> getAllSponsors() {
        return sponsorMapper.selectAllSponsors();
    }

    @Override
    public StaSponsor getSponsorById(Long id) {
        return sponsorMapper.selectSponsorById(id);
    }

    @Override
    public int addSponsor(StaSponsor sponsor) {
        return sponsorMapper.insertSponsor(sponsor);
    }

    @Override
    public int updateSponsor(StaSponsor sponsor) {
        return sponsorMapper.updateSponsorById(sponsor);
    }

    @Override
    public int deleteSponsor(Long id) {
        return sponsorMapper.deleteSponsorById(id);
    }
}
