package com.ruoyi.project.rule.enroll.controller;
import com.ruoyi.framework.web.domain.AjaxResult;
import com.ruoyi.project.rule.enroll.domain.StaEnroll;
import com.ruoyi.project.rule.enroll.service.StaEnrollService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/staEnroll")
public class StaEnrollController {
    @Autowired
    private StaEnrollService service;

    @PostMapping("/add")
    public AjaxResult addStaEnroll(StaEnroll staEnroll) {
        StaEnroll savedStaEnroll = service.save(staEnroll);
        return AjaxResult.success("新增成功");
    }

    @PutMapping("/update")
    public ResponseEntity<StaEnroll> updateStaEnroll(StaEnroll staEnroll) {
        Optional<StaEnroll> updatedStaEnroll = service.update(staEnroll);
        return updatedStaEnroll.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/byLeagueId")
    public AjaxResult getByLeagueId(Integer leagueId) {
        List<StaEnroll> staEnrolls = service.findByLeagueId(leagueId);
        return AjaxResult.success(staEnrolls);
    }
}
