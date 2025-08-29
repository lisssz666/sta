package com.ruoyi.mall.pay.controller;

import com.ruoyi.framework.web.domain.server.Sys;
import com.ruoyi.mall.pay.service.WxPayNotifyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

import static com.ruoyi.framework.datasource.DynamicDataSourceContextHolder.log;

/*
 * 验签 + 解密 + 业务处理类
 * */
@RestController
@Slf4j
public class WxPayNotifyController {

    @Autowired
    private WxPayNotifyService wxPayNotifyService;

    @PostMapping("/wxpay/notify")
    public String payNotify(HttpServletRequest request) throws IOException {
        log.info("Post微信支付通知进入");
        Map<String, String> headers = Collections.list(request.getHeaderNames())
                .stream()
                .collect(Collectors.toMap(h -> h, request::getHeader));
        String body = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
        log.info("Post微信支付通知进入body" +body);
        log.info("Post微信支付通知进入headers" +headers);
        String handle = wxPayNotifyService.handle(body, headers);
        log.info("返回handle"+handle);
        return handle;
    }
}
