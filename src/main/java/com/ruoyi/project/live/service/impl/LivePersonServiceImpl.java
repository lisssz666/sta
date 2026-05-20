package com.ruoyi.project.live.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.project.common.util.ScheduleLogUtil;
import com.ruoyi.project.game.address.domain.GameAddress;
import com.ruoyi.project.game.address.service.GameAddressService;
import com.ruoyi.project.live.domain.LivePerson;
import com.ruoyi.project.live.mapper.LivePersonMapper;
import com.ruoyi.project.live.service.LivePersonService;
import com.ruoyi.common.utils.file.FileUploadUtils;
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
 * 直播人员信息Service实现类
 * 描述：实现直播人员信息的业务操作方法
 */
@Service
public class LivePersonServiceImpl extends ServiceImpl<LivePersonMapper, LivePerson> implements LivePersonService {

    @Autowired
    private GameAddressService gameAddressService;
    
    //文件上传路径
    @Value("${spring.upload.path}")
    private String uploadImgPath;

    /**
     * 新增直播人员
     *
     * @param livePerson 直播人员信息
     * @return 新增是否成功
     */
    @Override
    public boolean saveLivePerson(LivePerson livePerson) throws IOException {
        // 设置默认值
        if (livePerson.getStatus() == null) {
            livePerson.setStatus(1); // 默认启用
        }
        if (livePerson.getIsHide() == null) {
            livePerson.setIsHide(false); // 默认不隐藏
        }
        if (livePerson.getLiveCount() == null) {
            livePerson.setLiveCount(0); // 默认直播场次为0
        }
        
        // 处理头像上传
        MultipartFile avatarFile = livePerson.getAvatarFile();
        if (avatarFile != null && !avatarFile.isEmpty()) {
            // 上传头像
            String avatarUrl = FileUploadUtils.upload(uploadImgPath, avatarFile);
            livePerson.setAvatarUrl(avatarUrl);
        }
        
        return save(livePerson);
    }

    /**
     * 更新直播人员信息
     *
     * @param livePerson 直播人员信息
     * @return 更新是否成功
     */
    @Override
    public boolean updateLivePersonById(LivePerson livePerson) throws IOException {
        // 处理头像上传
        MultipartFile avatarFile = livePerson.getAvatarFile();
        if (avatarFile != null && !avatarFile.isEmpty()) {
            // 上传头像
            String avatarUrl = FileUploadUtils.upload(uploadImgPath, avatarFile);
            livePerson.setAvatarUrl(avatarUrl);
        }
        return updateById(livePerson);
    }

    /**
     * 删除直播人员（逻辑删除）
     *
     * @param id 直播人员ID
     * @return 删除是否成功
     */
    @Override
    public boolean removeLivePersonById(Long id) {
        return removeById(id);
    }

    /**
     * 根据ID查询直播人员详情
     *
     * @param id 直播人员ID
     * @return 直播人员详情
     */
    @Override
    public LivePerson getLivePersonById(Long id) {
        LivePerson livePerson = getById(id);
        if (livePerson != null) {
            // 处理可用时间段
            handleAvailableTime(livePerson);
            
            // 添加场地信息
            addPitchInfo(livePerson);
        }
        return livePerson;
    }

    /**
     * 查询所有直播人员列表
     *
     * @return 直播人员列表
     */
    @Override
    public List<LivePerson> listLivePersons() {
        return list();
    }

    /**
     * 查询启用状态的直播人员列表
     *
     * @return 启用状态的直播人员列表
     */
    @Override
    public List<LivePerson> listActiveLivePersons() {
        return lambdaQuery()
                .eq(LivePerson::getStatus, 1)
                .eq(LivePerson::getIsHide, 0)
                .list();
    }
    
    /**
     * 处理直播人员的可用时间段（使用通用工具类）
     */
    private void handleAvailableTime(LivePerson livePerson) {
        List<Map<String, Object>> availableTimeList = ScheduleLogUtil.handleAvailableTime(livePerson.getScheduleLog());
        livePerson.setAvailableTime(availableTimeList);
    }
    
    /**
     * 添加场地信息到直播人员对象
     */
    private void addPitchInfo(LivePerson livePerson) {
        List<GameAddress> addresses = gameAddressService.getAllGameAddresses();
        List<Map<String, String>> pitchList = new ArrayList<>();
        for (GameAddress address : addresses) {
            Map<String, String> pitchMap = new HashMap<>();
            pitchMap.put("venueName", address.getVenueName());
            pitchList.add(pitchMap);
        }
        livePerson.setPitch(pitchList);
    }
}
