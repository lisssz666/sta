package com.ruoyi.project.game.organize.controller;

import com.ruoyi.framework.web.controller.BaseController;
import com.ruoyi.framework.web.domain.AjaxResult;
import com.ruoyi.project.game.organize.domain.StaOrganizeGameEntity;
import com.ruoyi.project.game.organize.service.IStaOrganizeGameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/game/StaOrganizeGame")
public class StaOrganizeGameController extends BaseController {

    @Autowired
    private IStaOrganizeGameService staOrganizeGameService;

    /** 列表 */
    @GetMapping("/list")
    public AjaxResult list(StaOrganizeGameEntity entity) {
        List<StaOrganizeGameEntity> list = staOrganizeGameService.list(entity);
        return AjaxResult.success("查询成功", list);
    }


    /** 新增 */
    @PostMapping("/add")
    public AjaxResult add(StaOrganizeGameEntity entity) {
        boolean ok = staOrganizeGameService.save(entity);
        return ok ? AjaxResult.success("新增成功", entity) : AjaxResult.error("新增失败");
    }

//    /** 更新 */
//    @PutMapping("/update")
//    public AjaxResult update( StaOrganizeGameEntity entity) {
//        boolean ok = staOrganizeGameService.updateById(entity);
//        return toAjax(ok ? 1 : 0);
//    }

    /** 删除 */
    @DeleteMapping("/delete")
    public AjaxResult delete(@RequestParam Long id) {
        boolean ok = staOrganizeGameService.removeById(id);
        return toAjax(ok ? 1 : 0);
    }
}
