package com.ruoyi.project.game.address.service;

import com.ruoyi.project.game.address.domain.GameAddress;
import com.ruoyi.project.game.address.mapper.GameAddressRepository;
import com.ruoyi.project.rule.enroll.domain.StaEnroll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GameAddressService {

    @Autowired
    private GameAddressRepository repository;

    public List<GameAddress> getAllGameAddresses() {
        return repository.findAll();
    }

    public Optional<GameAddress> getGameAddressById(Long id) {
        return repository.findById(id);
    }

    public GameAddress createGameAddress(GameAddress gameAddress) {
        return repository.save(gameAddress);
    }

    public void deleteGameAddress(Long id) {
        repository.deleteById(id);
    }

    public List<GameAddress> findByLeagueId(Long leagueId) {
        return repository.findByLeagueId(leagueId);
    }
}