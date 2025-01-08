package com.ruoyi.project.album.service;


import com.ruoyi.framework.web.domain.AjaxResult;
import com.ruoyi.project.album.domain.StaAlbum;

import java.io.IOException;
import java.util.List;

/**
 * 相册服务接口
 * 定义相册管理的基本操作。
 */
public interface StaAlbumService {

    /**
     * 创建新相册
     * @param album 相册实体
     * @return 操作影响的行数
     */
    int createAlbum(StaAlbum album);

    /**
     * 更新相册信息
     * @param album 相册实体
     * @return 操作影响的行数
     */
    int updateAlbum(StaAlbum album);

    /**
     * 删除相册
     * @param id 相册ID
     * @return 操作影响的行数
     */
    int deleteAlbum(Long id);

    /**
     * 根据联赛ID查询相册列表
     * @param leagueId 联赛ID
     * @return 相册列表
     */
    List<StaAlbum> getAlbumsByLeagueId(Long leagueId);

    /**
     * 上传照片到相册
     * @param photoPaths 照片路径列表
     * @return 操作影响的行数
     */
    AjaxResult uploadPhotos(List<StaAlbum> photoPaths) throws IOException;

    /**
     * 根据联相册ID查询照片列表
     * @param id 联赛ID
     * @return 相册列表
     */
    List<StaAlbum> getPicByAlbumId(Long id);
}
