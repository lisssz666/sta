package com.ruoyi.project.system.controller;

import com.ruoyi.framework.web.domain.AjaxResult;
import com.ruoyi.project.system.service.IWeChatLoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 微信登录控制器
 */
@RestController
@RequestMapping("/wechat")
public class WeChatLoginController {

    @Autowired
    private IWeChatLoginService weChatLoginService;

    /**
     * 微信登录
     *
     * @param params 包含code、encryptedData、iv的JSON对象
     * @return 登录结果，包含token和手机号
     */
    @PostMapping("/login")
    public AjaxResult weChatLogin(@RequestBody Map<String, String> params) {
        String code = params.get("code");
        String encryptedData = params.get("encryptedData");
        String iv = params.get("iv");

        if (code == null || code.isEmpty()) {
            return AjaxResult.error("参数code不能为空");
        }
        if (encryptedData == null || encryptedData.isEmpty()) {
            return AjaxResult.error("参数encryptedData不能为空");
        }
        if (iv == null || iv.isEmpty()) {
            return AjaxResult.error("参数iv不能为空");
        }

        try {
            return weChatLoginService.weChatLogin(code, encryptedData, iv);
        } catch (Exception e) {
            return AjaxResult.error("微信登录失败: " + e.getMessage());
        }
    }
}