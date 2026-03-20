package com.ruoyi.project.system.service;

import com.ruoyi.framework.web.domain.AjaxResult;

/**
 * 微信登录服务接口
 */
public interface IWeChatLoginService {

    /**
     * 微信登录
     *
     * @param code         微信登录code
     * @param encryptedData 加密数据
     * @param iv           加密向量
     * @return 登录结果，包含token和手机号
     * @throws Exception 异常信息
     */
    AjaxResult weChatLogin(String code, String encryptedData, String iv) throws Exception;
}