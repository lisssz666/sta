package com.ruoyi.mall.pay.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/wechat")
public class WeChatLoginController {

    @Value("${wechat.appid}")
    private String appId;

    @Value("${wechat.secret}")
    private String appSecret;

    private static final String WX_API_URL = "https://api.weixin.qq.com/sns/jscode2session";

    @GetMapping("/getPayInfoByCode")
    public ResponseEntity<Map<String, Object>> code2Session(@RequestParam String jsCode) {
        // 构建请求URL
        String url = String.format("%s?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code",
                WX_API_URL, appId, appSecret, jsCode);

        // 调用微信接口
        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);

        // 处理返回结果
        if (response == null) {
            return ResponseEntity.badRequest().body(createErrorResult("微信接口调用失败"));
        }

        Integer errcode = (Integer) response.get("errcode");
        if (errcode != null && errcode != 0) {
            return ResponseEntity.badRequest().body(response);
        }

        // 提取需要的字段返回给前端
        Map<String, Object> result = new HashMap<>();
        result.put("openid", response.get("openid"));
        result.put("session_key", response.get("session_key"));
        result.put("unionid", response.get("unionid"));

        return ResponseEntity.ok(result);
    }

    private Map<String, Object> createErrorResult(String message) {
        Map<String, Object> result = new HashMap<>();
        result.put("errcode", -1);
        result.put("errmsg", message);
        return result;
    }
}