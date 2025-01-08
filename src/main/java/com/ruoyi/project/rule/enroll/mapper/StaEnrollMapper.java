package com.ruoyi.project.rule.enroll.mapper;
import com.ruoyi.project.rule.enroll.domain.StaEnroll;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;

@Mapper
public interface StaEnrollMapper extends JpaRepository<StaEnroll, Integer>{
    List<StaEnroll> findByLeagueId(Integer leagueId);
}
