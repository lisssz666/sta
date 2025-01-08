package com.ruoyi.project.album.controller;

import com.ruoyi.framework.aspectj.lang.annotation.Log;
import com.ruoyi.framework.aspectj.lang.enums.BusinessType;
import com.ruoyi.framework.web.controller.BaseController;
import com.ruoyi.framework.web.domain.AjaxResult;
import com.ruoyi.framework.web.page.TableDataInfo;
import com.ruoyi.project.album.domain.StaAlbum;
import com.ruoyi.project.album.service.StaAlbumService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;
/**
 * 相册控制器
 * 提供相册管理的HTTP接口。
 */
@RestController
@RequestMapping("/staAlbum")
public class StaAlbumController extends BaseController{

    @Autowired
    private StaAlbumService albumService;

    /**
     * 创建新相册
     * @param album 相册实体
     * @return 操作结果信息
     */
    @PostMapping("/create")
    public AjaxResult createAlbum(StaAlbum album) {
        return toAjax(albumService.createAlbum(album)) ;

    }

    /**
     * 更新相册信息
     * @param album 相册实体
     * @return 操作结果信息
     */
    @PutMapping("/update")
    public AjaxResult updateAlbum(StaAlbum album) {
        return toAjax(albumService.updateAlbum(album));
    }

    /**
     * 删除相册
     * @param id 相册ID
     * @return 操作结果信息
     */
    @DeleteMapping("/delete")
    public AjaxResult deleteAlbum(@RequestParam Long id) {
        return toAjax(albumService.deleteAlbum(id));
    }

    /**
     * 根据联赛ID查询相册列表
     * @param leagueId 联赛ID
     * @return 相册列表
     */
    @GetMapping("/getAlbumsByLeagueId")
    public ResponseEntity<List<StaAlbum>> getAlbumsByLeagueId(Long leagueId) {
        List<StaAlbum> albums = albumService.getAlbumsByLeagueId(leagueId);
        return ResponseEntity.ok(albums);
    }

    /**
     * 根据相册ID查询照片列表
     * @param id 相册id
     * @return 相册列表
     */
    @GetMapping("/getPicByAlbumId")
    public ResponseEntity<List<StaAlbum>> getPicByAlbumId(Long id) {
        List<StaAlbum> albums = albumService.getPicByAlbumId(id);
        return ResponseEntity.ok(albums);
    }

    /**
     * 上传照片到相册
     * @param photoPaths 照片路径列表
     * @return 操作结果信息
     */
    @PostMapping("/uploadPhotos")
    public AjaxResult uploadPhotos(List<StaAlbum> photoPaths) throws IOException {
        return albumService.uploadPhotos(photoPaths);
    }
}

