package com.ruoyi.project.game.address.controller;

import com.ruoyi.framework.web.controller.BaseController;
import com.ruoyi.framework.web.domain.AjaxResult;
import com.ruoyi.project.game.address.domain.GameAddress;
import com.ruoyi.project.game.address.service.GameAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/game/addresses")
public class GameAddressController extends BaseController {

    @Autowired
    private GameAddressService service;

    @GetMapping("/list")
    public AjaxResult getAllGameAddresses() {
        return AjaxResult.success("查询成功",service.getAllGameAddresses());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GameAddress> getGameAddressById(@PathVariable Long id) {
        Optional<GameAddress> gameAddress = service.getGameAddressById(id);
        return gameAddress.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/add")
    public AjaxResult createGameAddress(GameAddress gameAddress) {
        return AjaxResult.success("新增成功",service.createGameAddress(gameAddress));
    }


    @DeleteMapping("/delete")
    public AjaxResult deleteGameAddress(Long id) {
        service.deleteGameAddress(id);
        return toAjax(1);
    }

    @GetMapping("/byLeagueId")
    public AjaxResult getByLeagueId(Long leagueId) {
        List<GameAddress> gameAddress = service.findByLeagueId(leagueId);
        return AjaxResult.success(gameAddress);
    }
}
