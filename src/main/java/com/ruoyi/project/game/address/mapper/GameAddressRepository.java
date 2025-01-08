package com.ruoyi.project.game.address.mapper;

import com.ruoyi.project.game.address.domain.GameAddress;
import com.ruoyi.project.rule.enroll.domain.StaEnroll;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

@Mapper
public interface GameAddressRepository extends JpaRepository<GameAddress, Long> {
    List<GameAddress> findByLeagueId(Long leagueId);
}
