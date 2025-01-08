package com.ruoyi.project.match.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;

import com.github.pagehelper.PageInfo;
import com.ruoyi.common.constant.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.framework.aspectj.lang.annotation.Log;
import com.ruoyi.framework.aspectj.lang.enums.BusinessType;
import com.ruoyi.project.match.domain.StaLeagueMatch;
import com.ruoyi.project.match.service.IStaLeagueMatchService;
import com.ruoyi.framework.web.controller.BaseController;
import com.ruoyi.framework.web.domain.AjaxResult;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.framework.web.page.TableDataInfo;
import org.springframework.web.multipart.MultipartFile;

/**
 * 联赛信息Controller
 * 
 * @author ruoyi
 * @date 2023-04-20
 */
@RestController
@RequestMapping("/match")
public class StaLeagueMatchController extends BaseController
{
    @Autowired
    private IStaLeagueMatchService staLeagueMatchService;

    /**
     * 查询联赛信息列表
     */
    @GetMapping("/list")
    public TableDataInfo list(StaLeagueMatch staLeagueMatch)
    {
        startPage();

//        Integer res =  staLeagueMatchService.updateAccessCount();
//        Integer accessCount = staLeagueMatchService.getAccessCount();
        List<StaLeagueMatch> list = staLeagueMatchService.selectStaLeagueMatchList(staLeagueMatch);
        return getDataTable(list);
    }

    /**
     * 导出联赛信息列表
     */
    @PostMapping("/export")
    public void export(HttpServletResponse response, StaLeagueMatch staLeagueMatch)
    {
        List<StaLeagueMatch> list = staLeagueMatchService.selectStaLeagueMatchList(staLeagueMatch);
        ExcelUtil<StaLeagueMatch> util = new ExcelUtil<StaLeagueMatch>(StaLeagueMatch.class);
        util.exportExcel(response, list, "联赛信息数据");
    }

    /**
     * 获取联赛信息详细信息
     */
    @GetMapping("/getStaLeagueMatch")
    public AjaxResult getInfo(Long id)
    {
        return AjaxResult.success(staLeagueMatchService.selectStaLeagueMatchById(id));
    }

    /**
     * 新增联赛信息
     */
    @PostMapping("/add")
    public AjaxResult add(StaLeagueMatch staLeagueMatch) throws IOException {
        int i = staLeagueMatchService.insertStaLeagueMatch(staLeagueMatch);
        System.out.print("add返回的id =="+i);
        return AjaxResult.success("操作完成",i);
    }
    /*@PostMapping("/add")
    public AjaxResult add(  @RequestParam(value = "leagueLogo", required = false) MultipartFile leagueLogo,
                            @RequestParam(value = "leagueBgImage", required = false) MultipartFile leagueBgImage,
                            StaLeagueMatch staLeagueMatch) throws IOException {
        return AjaxResult.success("操作完成",staLeagueMatchService.insertStaLeagueMatch(staLeagueMatch,leagueLogo,leagueBgImage));
    }*/

    /**
     * 修改联赛信息
     */
    @PutMapping("/edit")
    public AjaxResult edit(StaLeagueMatch staLeagueMatch) throws IOException
    {
        int i = staLeagueMatchService.updateStaLeagueMatch(staLeagueMatch);
        System.out.print("edit返回的id =="+i);
        return AjaxResult.success("操作完成",i);
    }

    /**
     * 删除联赛信息
     */
	@PostMapping("/delete")
    public AjaxResult remove(@RequestParam(value = "ids") Long[] ids)
    {
//        Long[] ids =map.get("ids");
        return toAjax(staLeagueMatchService.deleteStaLeagueMatchByIds(ids));
    }

    /**
     * 联赛信息
     */
//	@GetMapping("/getStaLeagueMatch")
//    public AjaxResult getStaLeagueMatch(Long id)
//    {
//        List<StaLeagueMatch> result = staLeagueMatchService.getStaLeagueMatch(id);
//        return AjaxResult.success(result);
//    }

    /**
     * 响应请求分页数据
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected TableDataInfo getDataTable1(List<?> list,Integer count)
    {
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(HttpStatus.SUCCESS);
        rspData.setMsg("查询成功");
        rspData.setData(list);
        rspData.setCount(count);
        if(null==list || list.isEmpty()) {
            rspData.setTotal(0L);
        }else {
            rspData.setTotal(new PageInfo(list).getTotal());
        }
        return rspData;
    }
}
