package com.ruoyi.project.video.service.impl;

import java.io.IOException;
import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.common.utils.file.MimeTypeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.ruoyi.project.video.mapper.StaVideoMapper;
import com.ruoyi.project.video.domain.StaVideo;
import com.ruoyi.project.video.service.IStaVideoService;
import org.springframework.web.multipart.MultipartFile;

/**
 * 视频录像文件Service业务层处理
 * 
 * @author ruoyi
 * @date 2023-03-28
 */
@Service
public class StaVideoServiceImpl implements IStaVideoService 
{
    private static final Logger log = LoggerFactory.getLogger(StaVideoServiceImpl.class);

    @Autowired
    private StaVideoMapper staVideoMapper;

    //文件上传路径
    @Value("${spring.upload.videopath}")
    private String uploadPath;

    //文件上传服务环境
    @Value("${spring.upload.server}")
    private String serverPath;

    /**
     * 查询视频录像文件
     * 
     * @param id 视频录像文件主键
     * @return 视频录像文件
     */
    @Override
    public StaVideo selectStaVideoById(Long id)
    {
        return staVideoMapper.selectStaVideoById(id);
    }

    /**
     * 查询视频录像文件列表
     * 
     * @param staVideo 视频录像文件
     * @return 视频录像文件
     */
    @Override
    public List<StaVideo> selectStaVideoList(StaVideo staVideo)
    {
        List<StaVideo> staVideos = staVideoMapper.selectStaVideoList(staVideo);
        staVideos.forEach(filePath -> filePath.setFilePath(serverPath+filePath.getFilePath()));
        return staVideos;
    }

    /**
     * 新增视频录像文件
     * 
     * @param staVideo 视频录像文件
     * @return 结果
     */
    @Override
    public int insertStaVideo(StaVideo staVideo)
    {
        return staVideoMapper.insertStaVideo(staVideo);
    }

    /**
     * 修改视频录像文件
     * 
     * @param staVideo 视频录像文件
     * @return 结果
     */
    @Override
    public int updateStaVideo(StaVideo staVideo)
    {
//        staVideo.setUpdateTime(DateUtils.getNowDate());
        return staVideoMapper.updateStaVideo(staVideo);
    }

    /**
     * 批量删除视频录像文件
     * 
     * @param ids 需要删除的视频录像文件主键
     * @return 结果
     */
    @Override
    public int deleteStaVideoByIds(Long[] ids)
    {
        return staVideoMapper.deleteStaVideoByIds(ids);
    }

    /**
     * 删除视频录像文件信息
     * 
     * @param id 视频录像文件主键
     * @return 结果
     */
    @Override
    public int deleteStaVideoById(Long id)
    {
        return staVideoMapper.deleteStaVideoById(id);
    }

    /**
     * 上传视频文件
     */
    @Override
    public Object uploadVideo(MultipartFile file) throws IOException{
        try
        {
            if (file ==null){
                return "文件不能为空";
            }
            log.info("上传的文件路径在这==============="+uploadPath);
            String fileName = file.getName();
            String result = FileUploadUtils.upload(uploadPath, file, MimeTypeUtils.DEFAULT_ALLOWED_EXTENSION);
//            result = result.replaceFirst ("/home/web/dist","");
            result = result.replaceFirst ("/Users/lisenshuai/Desktop","");
            log.info("图片访问路径++" +result);
            StaVideo staVideo = new StaVideo();
            staVideo.setFilePath(result);
            staVideo.setFileName(file.getOriginalFilename());
            staVideoMapper.insert(staVideo);
            return result;
        }
        catch (Exception e)
        {
            throw new IOException(e.getMessage(), e);
        }
    }
}
