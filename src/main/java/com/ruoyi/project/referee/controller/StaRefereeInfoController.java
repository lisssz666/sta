package com.ruoyi.project.referee.controller;

import com.ruoyi.framework.web.controller.BaseController;
import com.ruoyi.framework.web.domain.AjaxResult;
import com.ruoyi.project.referee.domain.StaRefereeInfoEntity;
import com.ruoyi.project.referee.service.IStaRefereeInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * 裁判信息Controller
 */

@RestController
@RequestMapping("/referee")
public class StaRefereeInfoController extends BaseController {

    @Autowired
    private IStaRefereeInfoService service;

    @GetMapping("/list")
    public AjaxResult list(StaRefereeInfoEntity query) {
        return AjaxResult.success(service.list(query));
    }

    @GetMapping("/getById")
    public AjaxResult get(Long id) {
        return AjaxResult.success(service.getById(id));
    }

    @PostMapping("/add")
    public AjaxResult add( StaRefereeInfoEntity entity) throws IOException {
        return toAjax(service.save(entity) ? 1 : 0);
    }

    @PutMapping("/update")
    public AjaxResult update(StaRefereeInfoEntity entity) {
        return toAjax(service.updateById(entity) ? 1 : 0);
    }

    @DeleteMapping("/delete")
    public AjaxResult delete(Long id) {
        return toAjax(service.removeById(id) ? 1 : 0);
    }
}
