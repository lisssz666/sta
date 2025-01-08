package com.ruoyi.project.album.mapper;

import com.ruoyi.project.album.domain.StaAlbum;

import java.util.List;

/**
 * 相册Mapper接口
 * 定义了相册管理相关的数据库操作。
 */
public interface StaAlbumMapper {

    /**
     * 插入新相册记录
     * @param album 相册实体
     * @return 操作影响的行数
     */
    int insertAlbum(StaAlbum album);

    /**
     * 更新相册记录
     * @param album 相册实体
     * @return 操作影响的行数
     */
    int updateAlbum(StaAlbum album);

    /**
     * 删除相册记录
     * @param id 相册ID
     * @return 操作影响的行数
     */
    int deleteAlbum(Long id);

    /**
     * 根据联赛ID查询相册列表
     * @param leagueId 联赛ID
     * @return 相册列表
     */
    List<StaAlbum> selectAlbumByLeagueId(Long leagueId);

    /**
     * 根据联相册ID查询照片列表
     * @param id 联赛ID
     * @return 相册列表
     */
    List<StaAlbum> getPicByAlbumId(Long id);
}
