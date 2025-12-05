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
    private ObjectMapper objectMapper;

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

        // 创建合并后的请求参数映射
        Map<String, Object> requestData = new HashMap<>();

        // 1. 处理JSON请求体
        if (jsonBody != null && !jsonBody.isEmpty()) {
            requestData.putAll(jsonBody);
            logger.info("从JSON请求体获取参数：{}", jsonBody);
        }

        // 2. 处理URL查询参数
        if (queryParams != null && !queryParams.isEmpty()) {
            // 转换所有字符串值为适当的类型
            for (Map.Entry<String, String> entry : queryParams.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                // 如果JSON请求体中已有该键，则跳过URL参数
                if (!requestData.containsKey(key)) {
                    requestData.put(key, convertParamValue(value));
                }
            }
            logger.info("从URL参数获取参数：{}", queryParams);
        }

        // 3. 处理Form表单提交
        if (contentType != null && contentType.contains("application/x-www-form-urlencoded")) {
            Map<String, String[]> formParams = request.getParameterMap();
            if (!formParams.isEmpty()) {
                for (Map.Entry<String, String[]> entry : formParams.entrySet()) {
                    String key = entry.getKey();
                    String[] values = entry.getValue();
                    // 如果JSON请求体或URL参数中已有该键，则跳过表单参数
                    if (!requestData.containsKey(key)) {
                        if (values.length == 1) {
                            requestData.put(key, convertParamValue(values[0]));
                        } else {
                            requestData.put(key, convertParamArray(values));
                        }
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
                    // 尝试解析为JSON
                    try {
                        Map<String, Object> parsedJson = objectMapper.readValue(requestBody, Map.class);
                        requestData.putAll(parsedJson);
                        logger.info("直接从请求体解析JSON参数：{}", parsedJson);
                    } catch (JsonProcessingException e) {
                        // 不是JSON格式，可能是表单数据
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
                // URL解码
                try {
                    value = URLDecoder.decode(value, "UTF-8");
                } catch (Exception e) {
                    // 解码失败则使用原始值
                }
                // 转换值类型
                requestData.put(key, convertParamValue(value));
            }
        }
    }

    /**
     * 转换参数值为适当的类型
     */
    private Object convertParamValue(String value) {
        // 尝试转换为Long
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e1) {
            // 尝试转换为Double
            try {
                return Double.parseDouble(value);
            } catch (NumberFormatException e2) {
                // 尝试转换为Boolean
                if ("true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value)) {
                    return Boolean.parseBoolean(value);
                }
                // 尝试转换为JSON数组或对象
                if ((value.startsWith("[") && value.endsWith("]")) ||
                        (value.startsWith("{") && value.endsWith("}"))) {
                    try {
                        return objectMapper.readValue(value, Object.class);
                    } catch (JsonProcessingException e3) {
                        // 转换失败则返回原始字符串
                        return value;
                    }
                }
                // 其他情况返回原始字符串
                return value;
            }
        }
    }

    /**
     * 转换参数数组
     */
    private List<Object> convertParamArray(String[] values) {
        List<Object> result = new ArrayList<>();
        for (String value : values) {
            result.add(convertParamValue(value));
        }
        return result;
    }
}