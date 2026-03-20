package com.ruoyi.project.live.controller;

import com.ruoyi.framework.web.controller.BaseController;
import com.ruoyi.framework.web.domain.AjaxResult;
import com.ruoyi.project.live.domain.LivePerson;
import com.ruoyi.project.live.service.LivePersonService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * 直播人员信息控制器
 * 描述：提供直播人员信息的CRUD操作接口
 */
@RestController
@RequestMapping("/livePerson")
public class LivePersonController extends BaseController {

    @Autowired
    private LivePersonService livePersonService;

    /**
     * 新增直播人员
     *
     * @param dto 直播人员信息
     * @return 新增是否成功
     * @throws IOException 文件上传异常
     */
    @PostMapping("/add")
    public AjaxResult addLivePerson(LivePerson dto) throws IOException {
        livePersonService.saveLivePerson(dto);
        return AjaxResult.success(dto.getId());
    }

    /**
     * 删除直播人员
     *
     * @param id 直播人员ID
     * @return 删除是否成功
     */
    @DeleteMapping("/delete")
    public AjaxResult delLivePerson(Long id) {
        return AjaxResult.success(livePersonService.removeLivePersonById(id));
    }

    /**
     * 修改直播人员信息
     *
     * @param dto 直播人员信息
     * @return 修改是否成功
     * @throws IOException 文件上传异常
     */
    @PutMapping("/update")
    public AjaxResult updLivePerson(LivePerson dto) throws IOException {
        return AjaxResult.success(livePersonService.updateLivePersonById(dto));
    }

    /**
     * 查询直播人员列表
     *
     * @return 直播人员列表
     */
    @GetMapping("/list")
    public AjaxResult listLivePersons() {
        return AjaxResult.success(livePersonService.listLivePersons());
    }

    /**
     * 查询启用状态的直播人员列表（用于前端展示）
     *
     * @return 启用状态的直播人员列表
     */
    @GetMapping("/listActive")
    public AjaxResult listActiveLivePersons() {
        return AjaxResult.success(livePersonService.listActiveLivePersons());
    }

    /**
     * 查询直播人员详情
     *
     * @param id 直播人员ID
     * @return 直播人员详情
     */
    @GetMapping("/getLivePersonById")
    public AjaxResult getLivePerson(Long id) {
        return AjaxResult.success(livePersonService.getLivePersonById(id));
    }
}
