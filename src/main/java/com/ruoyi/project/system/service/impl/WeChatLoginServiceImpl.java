package com.ruoyi.project.system.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.wechat.WeChatLoginUtils;
import com.ruoyi.framework.security.LoginUser;
import com.ruoyi.framework.security.service.TokenService;
import com.ruoyi.project.system.domain.SysUser;
import com.ruoyi.project.system.mapper.SysUserMapper;
import com.ruoyi.project.system.service.ISysUserService;
import com.ruoyi.project.system.service.IWeChatLoginService;
import com.ruoyi.framework.web.domain.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Map;

/**
 * 微信登录服务实现类
 */
@Service
public class WeChatLoginServiceImpl implements IWeChatLoginService {

    @Autowired
    private SysUserMapper userMapper;

    @Autowired
    private ISysUserService userService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private WeChatLoginUtils weChatLoginUtils;

    @Autowired
    private HttpServletRequest request;

    /**
     * 微信登录
     *
     * @param code         微信登录code
     * @param encryptedData 加密数据
     * @param iv           加密向量
     * @return 登录结果，包含token和手机号
     * @throws Exception 异常信息
     */
    @Override
    @Transactional
    public AjaxResult weChatLogin(String code, String encryptedData, String iv) throws Exception {
        // 1. 调用微信接口获取 openid 和 session_key
        Map<String, String> sessionInfo = weChatLoginUtils.code2Session(code);
        String openid = sessionInfo.get("openid");
        String sessionKey = sessionInfo.get("session_key");

        // 2. 解密 encryptedData 获取手机号
        JSONObject decryptedData = weChatLoginUtils.decryptEncryptedData(encryptedData, sessionKey, iv);
        String phoneNumber = weChatLoginUtils.getPhoneNumber(decryptedData);

        // 3. 根据手机号或 openid 查询用户
        SysUser user = getUserByPhoneOrOpenid(phoneNumber, openid);

        // 4. 如果用户不存在，创建新用户
        if (user == null) {
            user = createNewUser(phoneNumber, openid);
        } else {
            // 更新用户信息
            updateUserInfo(user, openid);
        }

        // 5. 生成登录凭证
        LoginUser loginUser = createLoginUser(user);
        String token = tokenService.createToken(loginUser);

        // 6. 返回结果
        AjaxResult result = AjaxResult.success();
        result.put("token", token);
        result.put("phonenumber", phoneNumber);
        result.put("userId", user.getUserId());
        result.put("userName", user.getUserName());
        return result;
    }

    /**
     * 根据手机号或 openid 查询用户
     *
     * @param phoneNumber 手机号
     * @param openid      微信openid
     * @return 用户对象
     */
    private SysUser getUserByPhoneOrOpenid(String phoneNumber, String openid) {
        // 先根据手机号查询
        SysUser phoneUser = userMapper.checkPhoneUnique(phoneNumber);
        if (phoneUser != null) {
            return phoneUser;
        }

        // 再根据 openid 查询
        SysUser openidUser = userMapper.selectUserByOpenid(openid);
        if (openidUser != null) {
            return openidUser;
        }

        return null;
    }

    /**
     * 创建新用户
     *
     * @param phoneNumber 手机号
     * @param openid      微信openid
     * @return 新创建的用户对象
     */
    private SysUser createNewUser(String phoneNumber, String openid) {
        SysUser user = new SysUser();

        // 设置用户信息
        user.setUserName(generateUserName(phoneNumber)); // 生成用户名
        user.setNickName("微信用户" + phoneNumber.substring(7)); // 生成昵称
        user.setPhonenumber(phoneNumber);
        user.setOpenid(openid);
        user.setPassword(SecurityUtils.encryptPassword("123456")); // 默认密码
        user.setStatus("0"); // 状态正常
        user.setDelFlag("0"); // 未删除
        user.setCreateTime(new Date());
        user.setCreateBy("sys");

        // 保存用户
        userService.registerUser(user);

        return user;
    }

    /**
     * 更新用户信息
     *
     * @param user   用户对象
     * @param openid 微信openid
     */
    private void updateUserInfo(SysUser user, String openid) {
        // 如果用户没有 openid，更新 openid
        if (StringUtils.isEmpty(user.getOpenid())) {
            user.setOpenid(openid);
            user.setUpdateTime(new Date());
            user.setUpdateBy("sys");
            userService.updateUser(user);
        }
    }

    /**
     * 生成用户名
     *
     * @param phoneNumber 手机号
     * @return 用户名
     */
    private String generateUserName(String phoneNumber) {
        // 生成用户名：wx_ + 手机号后8位
        return "wx_" + phoneNumber.substring(4);
    }

    /**
     * 创建登录用户对象
     *
     * @param user 用户对象
     * @return 登录用户对象
     */
    private LoginUser createLoginUser(SysUser user) {
        LoginUser loginUser = new LoginUser();
        loginUser.setUser(user);
        loginUser.setUserId(user.getUserId());
        loginUser.setDeptId(user.getDeptId());
        // 设置空的权限集合，避免序列化问题
        loginUser.setPermissions(null);
        return loginUser;
    }
}