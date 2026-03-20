package com.ruoyi.project.game.organize.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.*;

/**
 * 请求参数解析器 - 万能方法工具类
 * 支持JSON请求体、Form表单提交和URL参数的统一解析
 */
public class RequestParamParser {

    private static final Logger logger = LoggerFactory.getLogger(RequestParamParser.class);
    private final ObjectMapper objectMapper;

    /**
     * 构造函数，需要ObjectMapper实例
     */
    public RequestParamParser(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * 解析请求参数，支持多种请求格式
     * @param jsonBody JSON请求体
     * @param queryParams URL查询参数
     * @param contentType 请求头中的Content-Type
     * @param request HttpServletRequest对象
     * @return 合并后的请求参数映射
     */
    public Map<String, Object> parseRequestParams(
            Map<String, Object> jsonBody,
            Map<String, String> queryParams,
            String contentType,
            HttpServletRequest request) {

        Map<String, Object> requestData = new HashMap<>();

        // 1. 处理JSON请求体
        if (jsonBody != null && !jsonBody.isEmpty()) {
            requestData.putAll(jsonBody);
            logger.info("从JSON请求体获取参数：{}", jsonBody);
        }

        // 2. 处理URL查询参数
        if (queryParams != null && !queryParams.isEmpty()) {
            for (Map.Entry<String, String> entry : queryParams.entrySet()) {
                // 即使requestData中已经包含了这个键，也要处理URL参数
                // 因为URL参数可能包含复杂键名，需要合并到已有的结构中
                processParam(entry.getKey(), convertParamValue(entry.getValue()), requestData);
            }
            logger.info("从URL参数获取参数：{}", queryParams);
        }

        // 3. 处理Form表单提交
        if (contentType != null && contentType.contains("application/x-www-form-urlencoded")) {
            Map<String, String[]> formParams = request.getParameterMap();
            if (!formParams.isEmpty()) {
                for (Map.Entry<String, String[]> entry : formParams.entrySet()) {
                    // 即使requestData中已经包含了这个键，也要处理Form参数
                    // 因为Form参数可能包含复杂键名，需要合并到已有的结构中
                    String[] values = entry.getValue();
                    if (values.length == 1) {
                        processParam(entry.getKey(), convertParamValue(values[0]), requestData);
                    } else {
                        List<Object> convertedValues = new ArrayList<>();
                        for (String value : values) {
                            convertedValues.add(convertParamValue(value));
                        }
                        processParam(entry.getKey(), convertedValues, requestData);
                    }
                }
                logger.info("从Form表单获取参数：{}", formParams);
            }
        }

        // 4. 特殊处理：如果是POST请求但没有以上参数，尝试直接读取请求体
        if (requestData.isEmpty()) {
            try {
                String requestBody = readRequestBody(request);
                if (requestBody != null && !requestBody.isEmpty()) {
                    try {
                        Map<String, Object> parsedJson = objectMapper.readValue(requestBody, Map.class);
                        requestData.putAll(parsedJson);
                        logger.info("直接从请求体解析JSON参数：{}", parsedJson);
                    } catch (JsonProcessingException e) {
                        logger.info("直接从请求体解析表单参数：{}", requestBody);
                        parseFormData(requestBody, requestData);
                    }
                }
            } catch (Exception e) {
                logger.warn("读取请求体失败：{}", e.getMessage());
            }
        }

        return requestData;
    }

    /**
     * 读取请求体内容
     */
    private String readRequestBody(HttpServletRequest request) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (InputStream is = request.getInputStream()) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = is.read(buffer)) != -1) {
                sb.append(new String(buffer, 0, len, "UTF-8"));
            }
        }
        return sb.toString();
    }

    /**
     * 解析表单数据
     */
    private void parseFormData(String formData, Map<String, Object> requestData) {
        String[] pairs = formData.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=", 2);
            if (keyValue.length == 2) {
                String key = keyValue[0];
                String value = keyValue[1];
                try {
                    value = URLDecoder.decode(value, "UTF-8");
                } catch (Exception e) {
                    // 解码失败则使用原始值
                }
                processParam(key, convertParamValue(value), requestData);
            }
        }
    }

    /**
     * 转换参数值为适当的类型
     */
    private Object convertParamValue(String value) {
        // 尝试转换为数字类型
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e1) {
            try {
                return Double.parseDouble(value);
            } catch (NumberFormatException e2) {
                // 尝试转换为布尔类型
                if ("true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value)) {
                    return Boolean.parseBoolean(value);
                }
                // 尝试转换为JSON
                if ((value.startsWith("[") && value.endsWith("]")) || (value.startsWith("{") && value.endsWith("}"))) {
                    try {
                        return objectMapper.readValue(value, Object.class);
                    } catch (JsonProcessingException ignored) {
                        // 转换失败则返回原始字符串
                    }
                }
                return value;
            }
        }
    }

    /**
     * 处理参数，支持复杂键名
     */
    private void processParam(String key, Object value, Map<String, Object> requestData) {
        // 简单键名，直接设置
        if (!key.contains("[")) {
            requestData.put(key, value);
            return;
        }

        // 处理复杂键名，如teams[0][teamName]
        String[] parts = key.replaceAll("\\]", "").split("\\[");
        if (parts.length <= 1) {
            requestData.put(key, value);
            return;
        }

        // 从根Map开始
        Map<String, Object> currentMap = requestData;

        // 处理除了最后一部分之外的所有部分
        for (int i = 0; i < parts.length - 1; i++) {
            String part = parts[i];
            String nextPart = parts[i + 1];
            boolean nextIsNumber = nextPart.matches("\\d+");

            // 获取当前键对应的值
            Object nextObj = currentMap.get(part);

            if (nextObj == null) {
                // 如果当前键不存在，创建新的Map或List
                if (nextIsNumber) {
                    // 如果下一个键是数字，创建List
                    List<Map<String, Object>> newList = new ArrayList<>();
                    currentMap.put(part, newList);
                    nextObj = newList;
                } else {
                    // 如果下一个键不是数字，创建Map
                    Map<String, Object> newMap = new HashMap<>();
                    currentMap.put(part, newMap);
                    currentMap = newMap;
                    continue;
                }
            }

            if (nextObj instanceof List) {
                // 如果当前键对应的值是List
                List<?> rawList = (List<?>) nextObj;
                List<Map<String, Object>> currentList;

                // 确保List中的元素都是Map类型
                if (rawList.isEmpty()) {
                    // 空List，直接使用
                    currentList = (List<Map<String, Object>>) rawList;
                } else if (rawList.get(0) instanceof Map) {
                    // List中的元素是Map类型，直接使用
                    currentList = (List<Map<String, Object>>) rawList;
                } else {
                    // List中的元素不是Map类型，创建新的List
                    currentList = new ArrayList<>();
                    currentMap.put(part, currentList);
                }

                if (nextIsNumber) {
                    // 如果下一个键是数字，确保List有足够的元素
                    int index = Integer.parseInt(nextPart);
                    while (currentList.size() <= index) {
                        currentList.add(new HashMap<>());
                    }

                    // 更新currentMap为对应索引的Map
                    currentMap = currentList.get(index);
                    i++; // 跳过下一个键，因为已经处理了
                } else {
                    // 如果下一个键不是数字，使用List的最后一个元素
                    Map<String, Object> targetMap;
                    if (currentList.isEmpty()) {
                        targetMap = new HashMap<>();
                        currentList.add(targetMap);
                    } else {
                        targetMap = currentList.get(currentList.size() - 1);
                    }
                    currentMap = targetMap;
                }
            } else if (nextObj instanceof Map) {
                // 如果当前键对应的值是Map，直接使用
                currentMap = (Map<String, Object>) nextObj;
            } else {
                // 如果当前键对应的值不是List或Map，创建新的结构
                if (nextIsNumber) {
                    // 如果下一个键是数字，创建List
                    List<Map<String, Object>> newList = new ArrayList<>();
                    currentMap.put(part, newList);
                    
                    // 确保List有足够的元素
                    int index = Integer.parseInt(nextPart);
                    while (newList.size() <= index) {
                        newList.add(new HashMap<>());
                    }
                    
                    // 更新currentMap为对应索引的Map
                    currentMap = newList.get(index);
                    i++; // 跳过下一个键，因为已经处理了
                } else {
                    // 如果下一个键不是数字，创建Map
                    Map<String, Object> newMap = new HashMap<>();
                    currentMap.put(part, newMap);
                    currentMap = newMap;
                }
            }
        }

        // 设置最后一个键的值
        String lastPart = parts[parts.length - 1];
        currentMap.put(lastPart, value);
    }
}