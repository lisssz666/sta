package com.ruoyi.project.sponsor.controller;

import com.ruoyi.framework.web.controller.BaseController;
import com.ruoyi.framework.web.domain.AjaxResult;
import com.ruoyi.framework.web.page.TableDataInfo;
import com.ruoyi.project.sponsor.domain.StaSponsor;
import com.ruoyi.project.sponsor.service.StaSponsorService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "赞助商信息")
@RestController
@RequestMapping("/sponsors")
public class StaSponsorController extends BaseController{

    @Autowired
    private StaSponsorService sponsorService;

    // 查询所有赞助商信息
    @GetMapping("/list")
    public TableDataInfo getAllSponsors() {
        return getDataTable(sponsorService.getAllSponsors());
    }

    // 根据ID查询赞助商信息
    @GetMapping("/{id}")
    public AjaxResult getSponsorById(@PathVariable Long id) {
        return AjaxResult.success(sponsorService.getSponsorById(id));
    }

    // 添加新的赞助商
    @PostMapping("/add")
    public AjaxResult addSponsor(StaSponsor sponsor) {
        return toAjax(sponsorService.addSponsor(sponsor));
    }

    // 更新赞助商信息
    @PutMapping("/update")
    public AjaxResult updateSponsor(StaSponsor sponsor) {
        return toAjax(sponsorService.updateSponsor(sponsor));

    }

    // 删除赞助商
    @PostMapping("/delete")
    public AjaxResult deleteSponsor(@RequestParam Long id) {
        return toAjax( sponsorService.deleteSponsor(id));
    }

}

