package com.ruoyi.project.game.organize.service.impl;

import com.ruoyi.project.game.organize.domain.StaOrganizeGameEntity;
import com.ruoyi.project.game.organize.mapper.StaOrganizeGameMapper;
import com.ruoyi.project.game.organize.service.IStaOrganizeGameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 组织比赛Service实现类
 * 支持新的数据结构：前备注、球队数组(JSON)、后备注
 */
@Service
public class StaOrganizeGameServiceImpl implements IStaOrganizeGameService {
    @Autowired
    private StaOrganizeGameMapper repository;

    @Override
    public List<StaOrganizeGameEntity> list(StaOrganizeGameEntity entity) {
        return repository.selectAllList(entity);
    }

    @Override
    public boolean save(StaOrganizeGameEntity entity) {
        // 直接保存包含前备注、球队数组和后备注的完整实体
        return repository.insert(entity) > 0;
    }

    @Override
    public boolean removeById(Long id) {
        return repository.deleteById(id) > 0;
    }
    
    @Override
    public StaOrganizeGameEntity getById(Long id) {
        return repository.selectById(id);
    }
    
    @Override
    public boolean updateById(StaOrganizeGameEntity entity) {
        return repository.updateById(entity) > 0;
    }
    
    @Override
    public boolean addOrganizeGame(Map<String, Object> requestData) {
        // 验证请求数据
        if (requestData == null) {
            throw new IllegalArgumentException("请求数据不能为空");
        }
        
        // 打印完整请求数据，用于调试
        System.out.println("完整请求数据: " + requestData);
        System.out.println("teams参数类型: " + (requestData.get("teams") != null ? requestData.get("teams").getClass().getName() : "null"));
        System.out.println("teams参数值: " + requestData.get("teams"));
        
        // 提取数据
        String preRemark = (String) requestData.getOrDefault("preRemark", "");
        Object teamsObj = requestData.get("teams");
        List<Map<String, Object>> teams = null;
        
        // 解析teams参数，支持多种类型
        if (teamsObj instanceof String) {
            // 如果是字符串，尝试解析为JSON数组
            try {
                String teamsStr = (String) teamsObj;
                System.out.println("teams是字符串类型: " + teamsStr);
                if (teamsStr.startsWith("[") && teamsStr.endsWith("]")) {
                    // 使用Jackson或其他JSON库解析
                    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                    teams = mapper.readValue(teamsStr, new com.fasterxml.jackson.core.type.TypeReference<List<Map<String, Object>>>() {});
                }
            } catch (Exception e) {
                System.out.println("解析字符串teams失败: " + e.getMessage());
                throw new IllegalArgumentException("teams参数格式不正确，需要是有效的JSON数组字符串");
            }
        } else if (teamsObj instanceof List) {
            // 如果是直接的List类型，直接转换
            System.out.println("teams是List类型");
            teams = (List<Map<String, Object>>) teamsObj;
        } else if (teamsObj != null) {
            // 如果是其他类型，尝试转换为字符串再解析
            System.out.println("teams是其他类型: " + teamsObj.getClass().getName());
            try {
                String teamsStr = teamsObj.toString();
                if (teamsStr.startsWith("[") && teamsStr.endsWith("]")) {
                    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                    teams = mapper.readValue(teamsStr, new com.fasterxml.jackson.core.type.TypeReference<List<Map<String, Object>>>() {});
                }
            } catch (Exception e) {
                System.out.println("转换其他类型teams失败: " + e.getMessage());
                // 不抛出异常，继续检查
            }
        }
        
        // 特殊处理：如果teamsObj是LinkedHashMap等类型，可能是JSON解析问题
        if (teams == null && teamsObj != null) {
            System.out.println("尝试将teamsObj转换为List: " + teamsObj);
            // 创建一个新的List，并将teamsObj添加进去
            List<Map<String, Object>> tempList = new ArrayList<>();
            if (teamsObj instanceof Map) {
                // 如果是单个球队对象，包装成列表
                tempList.add((Map<String, Object>) teamsObj);
            } else {
                // 其他情况，尝试直接添加
                tempList.add(new HashMap<String, Object>() {{ put("data", teamsObj); }});
            }
            teams = tempList;
        }
        
        // 验证球队数组
        System.out.println("最终解析的teams: " + teams);
        if (teams == null || teams.isEmpty()) {
            throw new IllegalArgumentException("球队信息不能为空");
        }
        
        // 提取后备注
        String postRemark = (String) requestData.getOrDefault("postRemark", "");
        
        // 创建组织比赛实体
        StaOrganizeGameEntity entity = new StaOrganizeGameEntity();
        entity.setPreRemark(preRemark);
        entity.setTeams(teams);
        entity.setPostRemark(postRemark);
        // 设置默认状态为0（未开始）
        entity.setStatus(0);
        
        // 自动填充创建时间和更新时间
        Date currentTime = new Date();
        entity.setCreateTime(currentTime);
        entity.setUpdateTime(currentTime);
        
        // 保存数据
        return save(entity);
    }
    
    @Override
    public boolean repostOrganizeGame(Long id) {
        // 查询现有记录
        StaOrganizeGameEntity existingEntity = getById(id);
        if (existingEntity == null) {
            throw new IllegalArgumentException("记录不存在");
        }
        
        // 更新创建时间和更新时间为当前时间
        Date currentTime = new Date();
        existingEntity.setCreateTime(currentTime);
        existingEntity.setUpdateTime(currentTime);
        
        // 保存更新
        return updateById(existingEntity);
    }
    
    @Override
    public boolean updateOrganizeGameStatus(Long id, Integer status) {
        // 查询现有记录
        StaOrganizeGameEntity existingEntity = getById(id);
        if (existingEntity == null) {
            throw new IllegalArgumentException("记录不存在");
        }
        
        // 验证状态值是否有效（0-未开始，1-进行中，2-已完成）
        if (status < 0 || status > 2) {
            throw new IllegalArgumentException("状态值无效，必须为0、1或2");
        }
        
        // 更新状态和更新时间
        existingEntity.setStatus(status);
        existingEntity.setUpdateTime(new Date());
        
        // 保存更新
        return updateById(existingEntity);
    }
    
    @Override
    public boolean updateOrganizeGame(Map<String, Object> requestData) {
        // 验证请求数据
        if (requestData == null) {
            throw new IllegalArgumentException("请求数据不能为空");
        }
        // 获取并处理ID（支持Integer和Long类型）
        Object idObj = requestData.get("id");
        if (idObj == null) {
            throw new IllegalArgumentException("ID不能为空");
        }
        if (!(idObj instanceof Number)) {
            throw new IllegalArgumentException("ID必须是数字类型");
        }
        Long id = ((Number) idObj).longValue();
        // 查询现有记录
        StaOrganizeGameEntity existingEntity = getById(id);
        if (existingEntity == null) {
            throw new IllegalArgumentException("记录不存在");
        }
        
        // 更新字段
        if (requestData.containsKey("preRemark")) {
            existingEntity.setPreRemark((String) requestData.getOrDefault("preRemark", ""));
        }
        if (requestData.containsKey("teams")) {
            List<Map<String, Object>> teams = (List<Map<String, Object>>) requestData.get("teams");
            if (teams == null || teams.isEmpty()) {
                throw new IllegalArgumentException("球队信息不能为空");
            }
            existingEntity.setTeams(teams);
        }
        if (requestData.containsKey("postRemark")) {
            existingEntity.setPostRemark((String) requestData.getOrDefault("postRemark", ""));
        }
        if (requestData.containsKey("status")) {
            Integer status = (Integer) requestData.get("status");
            // 验证状态值是否有效（0-未开始，1-进行中，2-已完成）
            if (status < 0 || status > 2) {
                throw new IllegalArgumentException("状态值无效，必须为0、1或2");
            }
            existingEntity.setStatus(status);
        }
        
        // 更新更新时间
        existingEntity.setUpdateTime(new Date());
        
        // 保存更新
        return updateById(existingEntity);
    }
}