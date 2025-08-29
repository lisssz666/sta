package com.ruoyi.project.album.service.impl;

import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.common.utils.file.MimeTypeUtils;
import com.ruoyi.framework.web.domain.AjaxResult;
import com.ruoyi.project.album.domain.StaAlbum;
import com.ruoyi.project.album.mapper.StaAlbumMapper;
import com.ruoyi.project.album.service.StaAlbumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * 相册服务实现类
 * 实现相册管理的基本操作。
 */
@Service
public class StaAlbumServiceImpl implements StaAlbumService {

    //文件上传路径
    @Value("${spring.upload.path}")
    private String uploadPath;

    //文件上传服务环境
    @Value("${spring.upload.server}")
    private String serverPath;

    @Autowired
    private StaAlbumMapper albumMapper;

    @Override
    public int createAlbum(StaAlbum album) {
        return albumMapper.insertAlbum(album);
    }

    @Override
    public int updateAlbum(StaAlbum album) {
        return albumMapper.updateAlbum(album);
    }

    @Override
    public int deleteAlbum(Long id) {
        return albumMapper.deleteAlbum(id);
    }

    @Override
    public List<StaAlbum> getAlbumsByLeagueId(Long leagueId) {
        return albumMapper.selectAlbumByLeagueId(leagueId);
    }

    @Override
    public List<StaAlbum> getPicByAlbumId(Long id) {
        return albumMapper.getPicByAlbumId(id);
    }

    @Override
    public AjaxResult uploadPhotos(List<StaAlbum> photoPaths) throws IOException{
        try {
            for (StaAlbum sta : photoPaths){
                MultipartFile photoFile = sta.getPhotoFile();
                if (photoFile ==null && sta.getParentAlbumId() ==null){
                    return AjaxResult.error("参数为空");
                }
                String result = FileUploadUtils.upload(uploadPath+"/staimage", photoFile, MimeTypeUtils.DEFAULT_ALLOWED_EXTENSION);
//                result = result.replaceFirst ("/Users/lisenshuai/Desktop","");
                StaAlbum album = new StaAlbum();
                album.setParentAlbumId(sta.getParentAlbumId());
                album.setPhotoPath(result);
                albumMapper.insertAlbum(album);
                return AjaxResult.success("申请成功");
            }
        }
        catch (Exception e)
        {
            throw new IOException(e.getMessage(), e);
        }
        return AjaxResult.success("申请成功");
    }

}