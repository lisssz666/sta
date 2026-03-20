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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**

 */

@Service
public class StaRefereeInfoServiceImpl implements IStaRefereeInfoService {

    @Autowired
    private StaRefereeInfoMapper mapper;

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
        if (referee != null && referee.getAvatarPath() != null && !referee.getAvatarPath().isEmpty()) {
            // 检查头像路径是否已经包含服务器地址
            if (!referee.getAvatarPath().startsWith("http://") && !referee.getAvatarPath().startsWith("https://")) {
                referee.setAvatarPath(uploadServer + referee.getAvatarPath());
            }
            
            // 处理可用时间段
            handleAvailableTime(referee);
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
     * 处理裁判的可用时间段
     */
    private void handleAvailableTime(StaRefereeInfoEntity referee) {
        // 获取今天的日期
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        // 解析已选时间段
        Map<String, Set<String>> selectedTimeSlots = parseSelectedTimeSlots(referee.getScheduleLog());
        
        // 生成未来五天的全部时间段
        List<Map<String, Object>> availableTimeList = new ArrayList<>();
        
        // 处理未来五天的时间段
        for (int i = 0; i < 5; i++) {
            LocalDate currentDate = today.plusDays(i);
            String dateStr = currentDate.format(formatter);
            
            Map<String, Object> timeMap = new HashMap<>();
            timeMap.put("date", dateStr);
            // 添加周几字段
            timeMap.put("weekday", getWeekdayName(currentDate.getDayOfWeek().getValue()));
            timeMap.put("time", generateTimeSlotsWithStatus(dateStr, selectedTimeSlots));
            availableTimeList.add(timeMap);
        }
        
        // 将可用时间段信息设置到裁判对象中
        referee.setAvailableTime(availableTimeList);
    }
    
    /**
     * 获取周几的中文名称
     */
    private String getWeekdayName(int dayOfWeek) {
        String[] weekdays = {"", "周一", "周二", "周三", "周四", "周五", "周六", "周日"};
        return weekdays[dayOfWeek];
    }

    /**
     * 解析已选时间段
     */
    private Map<String, Set<String>> parseSelectedTimeSlots(String scheduleLog) {
        Map<String, Set<String>> selectedTimeSlots = new HashMap<>();
        
        if (scheduleLog != null && !scheduleLog.isEmpty()) {
            try {
                // 尝试解析为JSON格式
                JSONArray scheduleArray = JSON.parseArray(scheduleLog);
                LocalDate today = LocalDate.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                
                for (Object obj : scheduleArray) {
                    JSONObject scheduleObj = (JSONObject) obj;
                    String date = scheduleObj.getString("date");
                    
                    // 只保留今天及以后的时间段
                    try {
                        LocalDate slotDate = LocalDate.parse(date, formatter);
                        if (!slotDate.isBefore(today)) {
                            JSONArray timeArray = scheduleObj.getJSONArray("time");
                            Set<String> timeSlots = timeArray.stream()
                                    .map(time -> time.toString())
                                    .collect(Collectors.toSet());
                            selectedTimeSlots.put(date, timeSlots);
                        }
                    } catch (Exception e) {
                        // 日期解析失败，跳过
                    }
                }
            } catch (Exception e) {
                // JSON解析失败，尝试解析为非JSON格式，如"02-04 8:00-10:00 周三"
                try {
                    LocalDate today = LocalDate.now();
                    int currentYear = today.getYear();
                    
                    // 解析非JSON格式的时间字符串
                    String[] parts = scheduleLog.split(" ");
                    if (parts.length >= 2) {
                        // 解析日期部分 (MM-dd)
                        String datePart = parts[0];
                        // 解析时间段部分
                        String timeSlot = parts[1];
                        
                        // 构建完整日期 (yyyy-MM-dd)
                        String fullDateStr = currentYear + "-" + datePart;
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                        
                        try {
                            LocalDate slotDate = LocalDate.parse(fullDateStr, formatter);
                            if (!slotDate.isBefore(today)) {
                                // 添加时间段
                                Set<String> timeSlots = new HashSet<>();
                                timeSlots.add(timeSlot);
                                selectedTimeSlots.put(fullDateStr, timeSlots);
                            }
                        } catch (Exception ex) {
                            // 日期解析失败，跳过
                        }
                    }
                } catch (Exception ex) {
                    // 解析失败，返回空映射
                }
            }
        }
        
        return selectedTimeSlots;
    }

    /**
     * 生成时间段列表，包含状态标识
     */
    private List<Map<String, Object>> generateTimeSlotsWithStatus(String date, Map<String, Set<String>> selectedTimeSlots) {
        List<Map<String, Object>> timeSlots = new ArrayList<>();
        
        // 定义所有可能的时间段
        String[] allTimeSlots = {
            "8:00-10:00", "10:00-12:00", "12:00-14:00", "14:00-16:00", 
            "16:00-18:00", "18:00-20:00", "20:00-22:00", "22:00-24:00"
        };
        
        // 获取该日期已选的时间段
        Set<String> selectedSlots = selectedTimeSlots.getOrDefault(date, java.util.Collections.emptySet());
        
        // 获取当前日期和时间
        LocalDate today = LocalDate.now();
        LocalTime currentTime = LocalTime.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        // 解析传入的日期
        try {
            LocalDate slotDate = LocalDate.parse(date, dateFormatter);
            
            // 生成时间段列表，标记状态
            for (String slot : allTimeSlots) {
                // 检查时间段是否过期
                if (isTimeSlotExpired(slotDate, slot, today, currentTime)) {
                    // 时间段已过期，跳过
                    continue;
                }
                
                Map<String, Object> timeSlotMap = new HashMap<>();
                timeSlotMap.put("period", slot);
                // 1表示已选，0表示可选
                timeSlotMap.put("status", selectedSlots.contains(slot) ? 1 : 0);
                timeSlots.add(timeSlotMap);
            }
        } catch (Exception e) {
            // 日期解析失败，返回所有时间段
            for (String slot : allTimeSlots) {
                Map<String, Object> timeSlotMap = new HashMap<>();
                timeSlotMap.put("period", slot);
                timeSlotMap.put("status", selectedSlots.contains(slot) ? 1 : 0);
                timeSlots.add(timeSlotMap);
            }
        }
        
        return timeSlots;
    }
    
    /**
     * 检查时间段是否已过期
     * @param slotDate 时间段所属日期
     * @param timeSlot 时间段字符串（如"8:00-10:00"）
     * @param today 当前日期
     * @param currentTime 当前时间
     * @return 是否已过期
     */
    private boolean isTimeSlotExpired(LocalDate slotDate, String timeSlot, LocalDate today, LocalTime currentTime) {
        // 如果是今天之前的日期，已过期
        if (slotDate.isBefore(today)) {
            return true;
        }
        
        // 如果是今天，检查时间段是否已过
        if (slotDate.isEqual(today)) {
            try {
                // 解析时间段的开始时间
                String startTimeStr = timeSlot.split("-")[0];
                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("H:mm");
                LocalTime startTime = LocalTime.parse(startTimeStr, timeFormatter);
                
                // 如果当前时间大于等于时间段开始时间，已过期
                return currentTime.isAfter(startTime) || currentTime.equals(startTime);
            } catch (Exception e) {
                // 时间解析失败，视为未过期
                return false;
            }
        }
        
        // 未来日期，未过期
        return false;
    }
}
