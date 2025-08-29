package com.ruoyi.project.statistic.controller;

import java.util.Map;

import com.ruoyi.project.statistic.service.impl.TextToSpeechStatsExtractor;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.framework.aspectj.lang.annotation.Log;
import com.ruoyi.framework.aspectj.lang.enums.BusinessType;
import com.ruoyi.project.statistic.domain.StaPlayerStatistic;
import com.ruoyi.project.statistic.service.IStaPlayerStatisticService;
import com.ruoyi.framework.web.controller.BaseController;
import com.ruoyi.framework.web.domain.AjaxResult;

/**
 * 球员统计Controller
 * 
 * @author ruoyi
 * @date 2023-03-28
 */
@Api(tags = "球员统计")
@RestController
@RequestMapping("/statistic")
public class StaPlayerStatisticController extends BaseController
{
    @Autowired
    private IStaPlayerStatisticService staPlayerStatisticService;

    @Autowired
    private TextToSpeechStatsExtractor textToSpeechStatsExtractor;

    /**
     * 查询球员统计列表
     */
    @ApiOperation(value = "查询球员统计列表")
    @GetMapping("/list")
    public AjaxResult list(StaPlayerStatistic staPlayerStatistic)
    {
        return staPlayerStatisticService.selectStaPlayerStatisticList(staPlayerStatistic);
    }

    /**
     * 获取球员统计详细信息
     */
    @ApiOperation(value = "获取球员统计详细信息")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(staPlayerStatisticService.selectStaPlayerStatisticById(id));
    }

    @PostMapping(value = "/staStatistic")
    public AjaxResult selectStaPlayerStatistic(StaPlayerStatistic staPlayerStatistic)
    {
        return staPlayerStatisticService.selectStaPlayerStatistic(staPlayerStatistic);
    }

    /**
     * 新增球员统计
     */
    @ApiOperation(value = "新增球员统计")
    @Log(title = "球员统计", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(StaPlayerStatistic staPlayerStatistic)
    {
        return toAjax(staPlayerStatisticService.insertStaPlayerStatistic(staPlayerStatistic));
    }

    /**
     * 修改球员统计
     */
    @ApiOperation(value = "修改球员统计")
    @PutMapping("/edit")
    public AjaxResult edit(StaPlayerStatistic staPlayerStatistic)
    {
        return staPlayerStatisticService.updateStaPlayerStatistic(staPlayerStatistic);
    }

    /**
     * 删除球员统计
     */
    @ApiOperation(value = "删除球员统计")
    @Log(title = "球员统计", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(Map<String,Long[]> map)
    {
        Long[] ids =map.get("ids");
        return toAjax(staPlayerStatisticService.deleteStaPlayerStatisticByIds(ids));
    }

    /**
     * 识别语音文本统计数据
     */
    @PostMapping(value = "/textToSpeechSta")
    public AjaxResult textToSpeechSta(Long compeid)
    {
        return AjaxResult.success(textToSpeechStatsExtractor.textToSpeechSta(compeid));
    }


}
