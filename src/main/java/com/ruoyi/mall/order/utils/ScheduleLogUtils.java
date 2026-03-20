package com.ruoyi.mall.order.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ruoyi.project.referee.domain.StaRefereeInfoEntity;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 解析scheduleLog的工具类
 */
public class ScheduleLogUtils {
    
    /**
     * 解析现有的scheduleLog
     * @param refereeInfo 裁判信息
     * @param timeSlotsMap 时间段映射
     */
    public static void parseExistingScheduleLog(StaRefereeInfoEntity refereeInfo, Map<String, Set<String>> timeSlotsMap) {
        String existingLog = refereeInfo.getScheduleLog();
        if (org.springframework.util.StringUtils.isEmpty(existingLog)) {
            return;
        }
        
        try {
            // 尝试解析为JSON格式
            JSONArray existingArray = JSON.parseArray(existingLog);
            for (Object obj : existingArray) {
                JSONObject scheduleObj = (JSONObject) obj;
                String date = scheduleObj.getString("date");
                JSONArray timeArray = scheduleObj.getJSONArray("time");
                
                // 将时间段转换为集合
                Set<String> timeSlots = timeArray.stream()
                        .map(time -> time.toString())
                        .collect(java.util.stream.Collectors.toSet());
                
                // 添加到映射中
                timeSlotsMap.put(date, timeSlots);
            }
        } catch (Exception e) {
            // JSON解析失败，尝试解析为非JSON格式
            System.out.println("解析现有scheduleLog失败，尝试解析为非JSON格式: " + e.getMessage());
            parseNonJsonScheduleLog(existingLog, timeSlotsMap);
        }
    }
    
    /**
     * 解析scheduleLog并添加到映射中
     * @param scheduleLog 时间段日志
     * @param timeSlotsMap 时间段映射
     */
    public static void parseScheduleLog(String scheduleLog, Map<String, Set<String>> timeSlotsMap) {
        if (org.springframework.util.StringUtils.isEmpty(scheduleLog)) {
            return;
        }
        
        try {
            // 清理多余的斜杠
            String cleanedLog = scheduleLog.replaceAll("\\\\", "");
            JSONArray newArray = JSON.parseArray(cleanedLog);
            
            for (Object obj : newArray) {
                JSONObject scheduleObj = (JSONObject) obj;
                String date = scheduleObj.getString("date");
                JSONArray timeArray = scheduleObj.getJSONArray("time");
                
                // 将时间段转换为集合
                Set<String> timeSlots = timeArray.stream()
                        .map(time -> time.toString())
                        .collect(java.util.stream.Collectors.toSet());
                
                // 追加到现有时间段中
                if (timeSlotsMap.containsKey(date)) {
                    timeSlotsMap.get(date).addAll(timeSlots);
                } else {
                    timeSlotsMap.put(date, timeSlots);
                }
            }
        } catch (Exception e) {
            // 解析失败，尝试处理非JSON格式
            System.out.println("解析scheduleLog失败，尝试解析为非JSON格式: " + e.getMessage());
            parseNonJsonScheduleLog(scheduleLog, timeSlotsMap);
        }
    }
    
    /**
     * 解析非JSON格式的scheduleLog
     * @param scheduleLog 非JSON格式的scheduleLog
     * @param timeSlotsMap 时间段映射
     */
    public static void parseNonJsonScheduleLog(String scheduleLog, Map<String, Set<String>> timeSlotsMap) {
        try {
            // 尝试解析格式如"02-05 14:00-16:00 周四"的字符串
            String[] parts = scheduleLog.split(" ");
            if (parts.length >= 2) {
                String datePart = parts[0];
                String timeSlot = parts[1];
                
                // 构建完整日期 (yyyy-MM-dd)
                int currentYear = LocalDate.now().getYear();
                String fullDateStr = currentYear + "-" + datePart;
                
                // 添加到映射中
                timeSlotsMap.computeIfAbsent(fullDateStr, k -> new HashSet<>()).add(timeSlot);
            }
        } catch (Exception e) {
            System.out.println("解析非JSON格式scheduleLog失败: " + e.getMessage());
        }
    }
    
    /**
     * 过滤过期时间段并生成新的scheduleLog
     * @param timeSlotsMap 时间段映射
     * @return 更新后的scheduleLog
     */
    public static String generateUpdatedScheduleLog(Map<String, Set<String>> timeSlotsMap) {
        List<Map<String, Object>> scheduleList = new ArrayList<>();
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        // 遍历所有时间段，过滤过期的
        for (Map.Entry<String, Set<String>> entry : timeSlotsMap.entrySet()) {
            String dateStr = entry.getKey();
            Set<String> timeSlots = entry.getValue();
            
            try {
                LocalDate date = LocalDate.parse(dateStr, formatter);
                // 只保留今天及以后的时间段
                if (!date.isBefore(today)) {
                    Map<String, Object> scheduleMap = new HashMap<>();
                    scheduleMap.put("date", dateStr);
                    scheduleMap.put("time", new ArrayList<>(timeSlots));
                    scheduleList.add(scheduleMap);
                }
            } catch (Exception e) {
                // 日期解析失败，跳过
                System.out.println("日期解析失败: " + dateStr);
            }
        }
        
        // 转换为JSON字符串
        return JSON.toJSONString(scheduleList);
    }
    
    /**
     * 解析需要移除的scheduleLog
     * @param scheduleLog 时间段日志
     * @param slotsToRemove 存储需要移除的时间段映射
     */
    public static void parseSlotsToRemove(String scheduleLog, Map<String, Set<String>> slotsToRemove) {
        if (org.springframework.util.StringUtils.isEmpty(scheduleLog)) {
            return;
        }
        
        try {
            // 尝试解析为JSON格式
            String cleanedLog = scheduleLog.replace("\\", "");
            JSONArray scheduleArray = JSON.parseArray(cleanedLog);
            
            for (Object obj : scheduleArray) {
                JSONObject scheduleObj = (JSONObject) obj;
                String date = scheduleObj.getString("date");
                JSONArray timeArray = scheduleObj.getJSONArray("time");
                
                // 将时间段转换为集合
                Set<String> timeSlots = timeArray.stream()
                        .map(time -> time.toString())
                        .collect(java.util.stream.Collectors.toSet());
                
                slotsToRemove.put(date, timeSlots);
            }
        } catch (Exception e) {
            // JSON解析失败，尝试解析为非JSON格式
            System.out.println("解析需要移除的scheduleLog失败，尝试解析为非JSON格式: " + e.getMessage());
            try {
                // 尝试解析格式如"02-05 14:00-16:00 周四"的字符串
                String[] parts = scheduleLog.split(" ");
                if (parts.length >= 2) {
                    String datePart = parts[0];
                    String timeSlot = parts[1];
                    
                    // 构建完整日期 (yyyy-MM-dd)
                    int currentYear = LocalDate.now().getYear();
                    String fullDateStr = currentYear + "-" + datePart;
                    
                    // 添加到需要移除的映射中
                    Set<String> timeSlots = new HashSet<>();
                    timeSlots.add(timeSlot);
                    slotsToRemove.put(fullDateStr, timeSlots);
                }
            } catch (Exception ex) {
                System.out.println("解析非JSON格式scheduleLog失败: " + ex.getMessage());
            }
        }
    }
    
    /**
     * 从时间段映射中移除特定时间段
     * @param timeSlotsMap 时间段映射
     * @param slotsToRemove 需要移除的时间段映射
     */
    public static void removeTimeSlots(Map<String, Set<String>> timeSlotsMap, Map<String, Set<String>> slotsToRemove) {
        for (Map.Entry<String, Set<String>> entry : slotsToRemove.entrySet()) {
            String date = entry.getKey();
            Set<String> slots = entry.getValue();
            
            if (timeSlotsMap.containsKey(date)) {
                // 移除对应的时间段
                timeSlotsMap.get(date).removeAll(slots);
                
                // 如果该日期没有时间段了，从映射中移除
                if (timeSlotsMap.get(date).isEmpty()) {
                    timeSlotsMap.remove(date);
                }
            }
        }
    }
    
    /**
     * 放开已选择的时间段（从scheduleLog中移除对应的时间段）
     * @param refereeInfo 裁判信息
     * @param scheduleLog 时间段日志
     * @return 更新后的scheduleLog
     */
    public static String releaseScheduleSlots(StaRefereeInfoEntity refereeInfo, String scheduleLog) {
        // 1. 初始化时间段映射，用于存储日期到时间段集合的映射
        Map<String, Set<String>> timeSlotsMap = new HashMap<>();
        
        // 2. 解析现有的scheduleLog（如果存在）
        parseExistingScheduleLog(refereeInfo, timeSlotsMap);
        
        // 3. 解析需要移除的scheduleLog
        Map<String, Set<String>> slotsToRemove = new HashMap<>();
        parseSlotsToRemove(scheduleLog, slotsToRemove);
        
        // 4. 从现有时间段中移除需要释放的时间段
        removeTimeSlots(timeSlotsMap, slotsToRemove);
        
        // 5. 过滤过期时间段并生成新的scheduleLog
        return generateUpdatedScheduleLog(timeSlotsMap);
    }
}