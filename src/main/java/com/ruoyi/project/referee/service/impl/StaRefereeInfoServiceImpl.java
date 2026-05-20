package com.ruoyi.project.referee.service.impl;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.project.common.util.ScheduleLogUtil;
import com.ruoyi.project.game.address.domain.GameAddress;
import com.ruoyi.project.game.address.service.GameAddressService;
import com.ruoyi.project.referee.domain.StaRefereeInfoEntity;
import com.ruoyi.project.referee.mapper.StaRefereeInfoMapper;
import com.ruoyi.project.referee.service.IStaRefereeInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**

 */

@Service
public class StaRefereeInfoServiceImpl implements IStaRefereeInfoService {

    @Autowired
    private StaRefereeInfoMapper mapper;
    
    @Autowired
    private GameAddressService gameAddressService;

    //文件上传路径
    @Value("${spring.upload.path}")
    private String uploadImgPath;
    
    //文件服务器地址
    @Value("${spring.upload.server}")
    private String uploadServer;

    @Override
    public List<StaRefereeInfoEntity> list(StaRefereeInfoEntity entity) {
        List<StaRefereeInfoEntity> refereeList = mapper.selectAllList(entity);
        // 为每个裁判的头像路径拼接服务器地址
        for (StaRefereeInfoEntity referee : refereeList) {
            if (referee.getAvatarPath() != null && !referee.getAvatarPath().isEmpty()) {
                // 检查头像路径是否已经包含服务器地址
                if (!referee.getAvatarPath().startsWith("http://") && !referee.getAvatarPath().startsWith("https://")) {
                    referee.setAvatarPath(uploadServer + referee.getAvatarPath());
                }
            }
        }
        return refereeList;
    }

    @Override
    public StaRefereeInfoEntity getById(Long id) {
        StaRefereeInfoEntity referee = mapper.selectById(id);
        // 为头像路径拼接服务器地址
        if (referee != null) {
            if (referee.getAvatarPath() != null && !referee.getAvatarPath().isEmpty()) {
                // 检查头像路径是否已经包含服务器地址
                if (!referee.getAvatarPath().startsWith("http://") && !referee.getAvatarPath().startsWith("https://")) {
                    referee.setAvatarPath(uploadServer + referee.getAvatarPath());
                }
            }
            
            // 处理可用时间段
            handleAvailableTime(referee);
            
            // 添加场地信息
            addPitchInfo(referee);
        }
        return referee;
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
            // 设置状态默认值为1
            if (entity.getStatus() == null) {
                entity.setStatus("1");
            }
            return mapper.insert(entity) > 0;
        }
        catch (Exception e)
        {
            throw new IOException(e.getMessage(), e);
        }

    }

    @Override
    public boolean updateById(StaRefereeInfoEntity entity) throws IOException{
        MultipartFile avatar = entity.getAvatar();
        try {
            if (avatar != null && !avatar.isEmpty())
            {
                //上传头像
                String avatarPath = FileUploadUtils.upload(uploadImgPath, avatar);
                entity.setAvatarPath(avatarPath);
            }
            return mapper.updateById(entity) > 0;
        }
        catch (Exception e)
        {
            throw new IOException(e.getMessage(), e);
        }
        
    }

    @Override
    public boolean removeById(Long id) {
        return mapper.deleteById(id) > 0;
    }

    @Override
    public List<StaRefereeInfoEntity> listWithAvailableTime(StaRefereeInfoEntity entity) {
        List<StaRefereeInfoEntity> refereeList = mapper.selectAllList(entity);
        // 为每个裁判的头像路径拼接服务器地址
        for (StaRefereeInfoEntity referee : refereeList) {
            if (referee.getAvatarPath() != null && !referee.getAvatarPath().isEmpty()) {
                // 检查头像路径是否已经包含服务器地址
                if (!referee.getAvatarPath().startsWith("http://") && !referee.getAvatarPath().startsWith("https://")) {
                    referee.setAvatarPath(uploadServer + referee.getAvatarPath());
                }
            }
            
            // 处理可用时间段
            handleAvailableTime(referee);
        }
        return refereeList;
    }

    /**
     * 处理裁判的可用时间段（使用通用工具类）
     */
    private void handleAvailableTime(StaRefereeInfoEntity referee) {
        List<Map<String, Object>> availableTimeList = ScheduleLogUtil.handleAvailableTime(referee.getScheduleLog());
        referee.setAvailableTime(availableTimeList);
    }
    
    /**
     * 添加场地信息到裁判对象
     */
    private void addPitchInfo(StaRefereeInfoEntity referee) {
        List<GameAddress> addresses = gameAddressService.getAllGameAddresses();
        List<Map<String, String>> pitchList = new ArrayList<>();
        for (GameAddress address : addresses) {
            Map<String, String> pitchMap = new HashMap<>();
            pitchMap.put("venueName", address.getVenueName());
            pitchList.add(pitchMap);
        }
        referee.setPitch(pitchList);
    }
}
