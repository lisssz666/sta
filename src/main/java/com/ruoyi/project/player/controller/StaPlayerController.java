package com.ruoyi.project.player.controller;

import java.io.IOException;
import java.util.List;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.project.player.domain.StaPlayer;
import com.ruoyi.project.player.service.IStaPlayerService;
import com.ruoyi.framework.web.controller.BaseController;
import com.ruoyi.framework.web.domain.AjaxResult;
import com.ruoyi.framework.web.page.TableDataInfo;

/**
 * 运动员信息Controller
 * 
 * @author ruoyi
 * @date 2023-03-28
 */
@Api(tags = "运动员信息")
@RestController
@RequestMapping("/player")
public class StaPlayerController extends BaseController
{
    @Autowired
    private IStaPlayerService staPlayerService;

    /**
     * 查询运动员信息列表
     */
    @ApiOperation(value = "查询运动员信息列表")
    @GetMapping("/list")
    public TableDataInfo list(StaPlayer staPlayer)
    {
        startPage();
        List<StaPlayer> list = staPlayerService.selectStaPlayerList(staPlayer);
        return getDataTable(list);
    }



    /**
     * 获取运动员信息详细信息
     */
    @ApiOperation(value = "获取运动员信息详细信息")
    @GetMapping("getPlayerById")
    public AjaxResult getInfo(@RequestParam(value = "id") Long id)
    {
        return AjaxResult.success(staPlayerService.selectStaPlayerById(id));
    }

    /**
     * 新增运动员信息
     */
    @ApiOperation(value = "新增运动员信息")
    @PostMapping("/add")
    public AjaxResult add(StaPlayer staPlayer) throws IOException {
        return AjaxResult.success("操作成功",staPlayerService.insertStaPlayer(staPlayer));
    }

    /**
     * 修改运动员信息
     */
    @ApiOperation(value = "修改运动员信息")
    @PutMapping("/edit")
    public AjaxResult edit(StaPlayer staPlayer)
    {
        return toAjax(staPlayerService.updateStaPlayer(staPlayer));
    }

    /**
     * 删除运动员信息
     */
    @ApiOperation(value = "删除运动员信息")
    @PostMapping("/delete")
    public AjaxResult remove(@RequestParam(value = "ids") Long[] ids)
    {
        return toAjax(staPlayerService.deleteStaPlayerByIds(ids));
    }
}
