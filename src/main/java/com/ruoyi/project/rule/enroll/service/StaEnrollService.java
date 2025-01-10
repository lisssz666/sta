package com.ruoyi.project.rule.enroll.service;

import com.ruoyi.project.rule.enroll.domain.StaEnroll;
import com.ruoyi.project.rule.enroll.mapper.StaEnrollMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class StaEnrollService {
    @Autowired
    private StaEnrollMapper repository;

    public StaEnroll save(StaEnroll staEnroll) {
        return this.repository.save(staEnroll);
    }

    public Optional<StaEnroll> update(StaEnroll staEnroll) {
        Integer id = staEnroll.getId();
        return this.repository.findById(id).map(existingStaEnroll -> {
            existingStaEnroll.setStartDate(staEnroll.getStartDate());
            existingStaEnroll.setEndDate(staEnroll.getEndDate());
            existingStaEnroll.setTeams(staEnroll.getTeams());
            existingStaEnroll.setCapacity(staEnroll.getCapacity());
            existingStaEnroll.setContact(staEnroll.getContact());
            existingStaEnroll.setPhone(staEnroll.getPhone());
            existingStaEnroll.setEmail(staEnroll.getEmail());
            existingStaEnroll.setInstructions(staEnroll.getInstructions());
            existingStaEnroll.setLeagueId(staEnroll.getLeagueId());
            return (StaEnroll)this.repository.save(existingStaEnroll);
        });
    }
    public List<StaEnroll> findByLeagueId(Integer leagueId) {
        return this.repository.findByLeagueId(leagueId);
    }
}
