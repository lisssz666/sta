package com.ruoyi.common.utils.wechat;

import com.alibaba.fastjson.JSONObject;
import com.ruoyi.common.utils.http.HttpUtils;
import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.AlgorithmParameters;
import java.security.Security;
import java.util.HashMap;
import java.util.Map;

/**
 * 微信登录工具类
 */
@Component
public class WeChatLoginUtils {

    /**
     * 微信小程序 appId
     */
    @Value("${wechat.appid}")
    private String appId;

    /**
     * 微信小程序 appSecret
     */
    @Value("${wechat.secret}")
    private String appSecret;

    /**
     * 微信 code2Session 接口地址
     */
    private static final String CODE2SESSION_URL = "https://api.weixin.qq.com/sns/jscode2session";

    static {
        // 注册 BouncyCastle 作为安全提供者
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * 调用微信 code2Session 接口获取 openid 和 session_key
     *
     * @param code 前端传来的 code
     * @return 包含 openid 和 session_key 的 map
     * @throws Exception 异常信息
     */
    public Map<String, String> code2Session(String code) throws Exception {
        // 构建请求参数
        StringBuilder paramBuilder = new StringBuilder();
        paramBuilder.append("appid=").append(appId)
                .append("&secret=").append(appSecret)
                .append("&js_code=").append(code)
                .append("&grant_type=authorization_code");

        // 发送请求
        String result = HttpUtils.sendGet(CODE2SESSION_URL, paramBuilder.toString());

        // 解析响应
        JSONObject jsonObject = JSONObject.parseObject(result);

        // 检查是否有错误
        if (jsonObject.containsKey("errcode")) {
            int errCode = jsonObject.getIntValue("errcode");
            String errMsg = jsonObject.getString("errmsg");
            throw new Exception("微信接口错误: " + errCode + " - " + errMsg);
        }

        // 提取 openid 和 session_key
        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("openid", jsonObject.getString("openid"));
        resultMap.put("session_key", jsonObject.getString("session_key"));
        resultMap.put("unionid", jsonObject.getString("unionid")); // 可能不存在

        return resultMap;
    }

    /**
     * 解密 encryptedData 获取手机号
     *
     * @param encryptedData 加密数据
     * @param sessionKey    会话密钥
     * @param iv            加密向量
     * @return 解密后的 JSON 对象
     * @throws Exception 异常信息
     */
    public JSONObject decryptEncryptedData(String encryptedData, String sessionKey, String iv) throws Exception {
        // Base64 解码
        byte[] encryptedDataBytes = Base64.decodeBase64(encryptedData);
        byte[] sessionKeyBytes = Base64.decodeBase64(sessionKey);
        byte[] ivBytes = Base64.decodeBase64(iv);

        // 初始化加密器
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");
        SecretKeySpec keySpec = new SecretKeySpec(sessionKeyBytes, "AES");
        AlgorithmParameters params = AlgorithmParameters.getInstance("AES");
        params.init(new IvParameterSpec(ivBytes));
        cipher.init(Cipher.DECRYPT_MODE, keySpec, params);

        // 解密
        byte[] decryptedBytes = cipher.doFinal(encryptedDataBytes);
        String decryptedString = new String(decryptedBytes, "UTF-8");

        // 解析为 JSON 对象
        return JSONObject.parseObject(decryptedString);
    }

    /**
     * 从解密后的数据中提取手机号
     *
     * @param decryptedData 解密后的数据
     * @return 手机号
     */
    public String getPhoneNumber(JSONObject decryptedData) {
        return decryptedData.getString("phoneNumber");
    }
}