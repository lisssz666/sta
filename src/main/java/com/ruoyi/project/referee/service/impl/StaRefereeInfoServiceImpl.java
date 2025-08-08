package com.ruoyi.project.referee.service.impl;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.project.referee.domain.StaRefereeInfoEntity;
import com.ruoyi.project.referee.mapper.StaRefereeInfoMapper;
import com.ruoyi.project.referee.service.IStaRefereeInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**

 */

@Service
public class StaRefereeInfoServiceImpl implements IStaRefereeInfoService {

    @Autowired
    private StaRefereeInfoMapper mapper;

    //文件上传路径
    @Value("${spring.upload.path}")
    private String uploadImgPath;

    @Override
    public List<StaRefereeInfoEntity> list(StaRefereeInfoEntity entity) {
        return mapper.selectAllList(entity);
    }

    @Override
    public StaRefereeInfoEntity getById(Long id) {
        return mapper.selectById(id);
    }

    @Override
    public boolean save(StaRefereeInfoEntity entity) throws IOException{
        MultipartFile avatar = entity.getAvatar();
        try {
            if (avatar != null && !avatar.isEmpty())
            {
                //上传头像
                String avatarPath = FileUploadUtils.upload(uploadImgPath, avatar);
                entity.setAvatarPath(avatarPath);
            }
            return mapper.insert(entity) > 0;
        }
        catch (Exception e)
        {
            throw new IOException(e.getMessage(), e);
        }

    }

    @Override
    public boolean updateById(StaRefereeInfoEntity entity) {
        return mapper.updateById(entity) > 0;
    }

    @Override
    public boolean removeById(Long id) {
        return mapper.deleteById(id) > 0;
    }
}
