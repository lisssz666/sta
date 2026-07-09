package com.ruoyi.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Token解析工具类
 * 用于从请求头的Authorization中解析用户信息
 */
@Component
public class TokenUtil {

    /**
     * 从token中解析用户ID
     *
     * @param token JWT token
     * @return 用户ID，如果解析失败返回null
     */
    public static Long getUserIdFromToken(String token) {
        if (!StringUtils.hasText(token)) {
            return null;
        }

        try {
            // 移除Bearer前缀（如果存在）
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            // JWT token由三部分组成：header.payload.signature
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                return null;
            }

            // 解析payload部分（第二部分）
            String payload = Base64Utils.decodeToString(parts[1]);
            JSONObject jsonObject = JSON.parseObject(payload);

            // 尝试从不同字段获取用户ID
            if (jsonObject.containsKey("userId")) {
                return jsonObject.getLong("userId");
            } else if (jsonObject.containsKey("user_id")) {
                return jsonObject.getLong("user_id");
            } else if (jsonObject.containsKey("uid")) {
                return jsonObject.getLong("uid");
            } else if (jsonObject.containsKey("id")) {
                return jsonObject.getLong("id");
            }

        } catch (Exception e) {
            // token解析失败，返回null
        }

        return null;
    }

    /**
     * 简单的Base64解码工具
     */
    public static class Base64Utils {
        public static String decodeToString(String encoded) {
            try {
                // 处理URL安全的Base64编码
                encoded = encoded.replace('-', '+').replace('_', '/');
                // 补充padding
                int padding = (4 - encoded.length() % 4) % 4;
                if (padding > 0) {
                    encoded += "====".substring(0, padding);
                }
                byte[] decodedBytes = java.util.Base64.getDecoder().decode(encoded);
                return new String(decodedBytes, java.nio.charset.StandardCharsets.UTF_8);
            } catch (Exception e) {
                return null;
            }
        }
    }
}
