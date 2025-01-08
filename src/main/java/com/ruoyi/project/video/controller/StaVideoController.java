package com.ruoyi.project.video.controller;

import java.io.IOException;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.framework.aspectj.lang.annotation.Log;
import com.ruoyi.framework.aspectj.lang.enums.BusinessType;
import com.ruoyi.project.video.domain.StaVideo;
import com.ruoyi.project.video.service.IStaVideoService;
import com.ruoyi.framework.web.controller.BaseController;
import com.ruoyi.framework.web.domain.AjaxResult;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.framework.web.page.TableDataInfo;
import org.springframework.web.multipart.MultipartFile;

/**
 * 视频录像文件Controller
 * 
 * @author ruoyi
 * @date 2023-03-28
 */
@RestController
@RequestMapping("/video")
public class StaVideoController extends BaseController
{
    @Autowired
    private IStaVideoService staVideoService;

    /**
     * 查询视频录像文件列表
     */
    @GetMapping("/list")
    public TableDataInfo list(StaVideo staVideo)
    {
        startPage();
        List<StaVideo> list = staVideoService.selectStaVideoList(staVideo);
        return getDataTable(list);
    }

    /**
     * 获取视频录像文件详细信息
     */
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(staVideoService.selectStaVideoById(id));
    }

    /**
     * 新增视频录像文件
     */
    @Log(title = "视频录像文件", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(StaVideo staVideo)
    {
        return toAjax(staVideoService.insertStaVideo(staVideo));
    }

    /**
     * 修改视频录像文件
     */
    @Log(title = "视频录像文件", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody StaVideo staVideo)
    {
        return toAjax(staVideoService.updateStaVideo(staVideo));
    }

    /**
     * 删除视频录像文件
     */
    @Log(title = "视频录像文件", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(Long[] ids)
    {
        return toAjax(staVideoService.deleteStaVideoByIds(ids));
    }

    /**
     * 上传视频文件
     */
    @PostMapping("/uploadVideo")
    public AjaxResult uploadVideo(MultipartFile file) throws IOException {
        return AjaxResult.success("上传成功",staVideoService.uploadVideo(file));
    }
}
