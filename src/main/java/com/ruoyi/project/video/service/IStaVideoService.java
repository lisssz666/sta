package com.ruoyi.project.video.service;

import java.io.IOException;
import java.util.List;

import com.ruoyi.framework.web.domain.AjaxResult;
import com.ruoyi.project.video.domain.StaVideo;
import org.springframework.web.multipart.MultipartFile;

/**
 * 视频录像文件Service接口
 * 
 * @author ruoyi
 * @date 2023-03-28
 */
public interface IStaVideoService 
{
    /**
     * 查询视频录像文件
     * 
     * @param id 视频录像文件主键
     * @return 视频录像文件
     */
    public StaVideo selectStaVideoById(Long id);

    /**
     * 查询视频录像文件列表
     * 
     * @param staVideo 视频录像文件
     * @return 视频录像文件集合
     */
    public List<StaVideo> selectStaVideoList(StaVideo staVideo);

    /**
     * 新增视频录像文件
     * 
     * @param staVideo 视频录像文件
     * @return 结果
     */
    public int insertStaVideo(StaVideo staVideo);

    /**
     * 修改视频录像文件
     * 
     * @param staVideo 视频录像文件
     * @return 结果
     */
    public int updateStaVideo(StaVideo staVideo);

    /**
     * 批量删除视频录像文件
     * 
     * @param ids 需要删除的视频录像文件主键集合
     * @return 结果
     */
    public int deleteStaVideoByIds(Long[] ids);

    /**
     * 删除视频录像文件信息
     * 
     * @param id 视频录像文件主键
     * @return 结果
     */
    public int deleteStaVideoById(Long id);

    /**
     * 上传视频文件
     */
    Object uploadVideo(MultipartFile file, String leagueId, String gameId) throws IOException;

    AjaxResult fullPlayBlack(String leagueId, String gameId) throws Exception;
}
