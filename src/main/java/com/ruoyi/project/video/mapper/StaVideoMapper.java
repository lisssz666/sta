package com.ruoyi.project.video.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.project.video.domain.StaVideo;

/**
 * 视频录像文件Mapper接口
 * 
 * @author ruoyi
 * @date 2023-03-28
 */
public interface StaVideoMapper extends BaseMapper<StaVideo>
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
     * 删除视频录像文件
     * 
     * @param id 视频录像文件主键
     * @return 结果
     */
    public int deleteStaVideoById(Long id);

    /**
     * 批量删除视频录像文件
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteStaVideoByIds(Long[] ids);
}
