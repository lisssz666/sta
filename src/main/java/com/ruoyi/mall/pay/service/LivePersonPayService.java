package com.ruoyi.mall.pay.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.ruoyi.mall.order.domain.LivePersonOrder;

import java.io.*;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 直播人员支付服务类
 * <p>
 * 提供直播人员支付场景的核心业务逻辑处理，包括：
 * 1. openid获取与缓存
 * 2. 预支付订单创建
 * 3. 支付参数生成与签名
 * 4. 支付通知处理
 * 5. 订单查询与退款
 * </p>
 * 
 * @author ruoyi
 * @version 1.0.0
 * @since 2026-04-27
 */
@Service
public class LivePersonPayService {

    @Value("${wechat.appid}")
    private String appId;

    @Value("${wechat.secret}")
    private String appSecret;

    @Value("${wechat.pay.mchid}")
    private String merchantId;

    @Value("${wechat.pay.serial_no}")
    private String certSerialNo;

    @Value("${wechat.liveperson_pay.notify_url}")
    private String notifyUrl;

    @Value("${wechat.pay.APIv3_key}")
    private String APIv3Key;

    private static final String PRIVATE_KEY_PATH = "cert/apiclient_key.pem";
    private static final String WX_API_URL = "https://api.mch.weixin.qq.com/v3/pay/transactions/jsapi";
    private static final String WX_QUERY_URL = "https://api.mch.weixin.qq.com/v3/pay/transactions/out-trade-no/%s";
    private static final String WX_REFUND_URL = "https://api.mch.weixin.qq.com/v3/refund/domestic/refunds";

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private com.ruoyi.mall.order.service.LivePersonOrderService livePersonOrderService;

    /**
     * 获取支付参数
     * <p>
     * 直播人员支付核心业务逻辑入口：
     * 1. 通过小程序code获取用户openid
     * 2. 创建预支付订单获取prepay_id
     * 3. 生成小程序支付所需参数
     * </p>
     *
     * @param code 微信小程序登录凭证code
     * @param amount 支付金额（单位：元）
     * @param outTradeNo 商户订单号
     * @param description 订单描述
     * @return 支付参数
     */
    public Map<String, String> getPaymentParams(String code, String amount, String outTradeNo, String description) throws Exception {
        // 1. 获取openid
        String openid = getOpenidByCode(code);
        System.out.println("直播人员支付获取openid成功：" + openid);

        // 2. 获取prepay_id
        String prepayId = getPrepayId(openid, amount, outTradeNo, description);
        System.out.println("直播人员支付获取prepay_id成功：" + prepayId);

        // 3. 生成支付参数
        return generatePaymentParams(prepayId);
    }

    /**
     * 通过code获取openid
     * <p>
     * 使用微信小程序登录凭证code换取用户openid
     * 实现了Redis缓存机制，避免重复调用微信API
     * </p>
     *
     * @param code 微信小程序登录凭证code
     * @return 用户openid
     */
    private String getOpenidByCode(String code) throws Exception {
        // 1. 先从Redis缓存中查找
        String cacheKey = "liveperson:code:" + code;
        String openid = redisTemplate.opsForValue().get(cacheKey);
        if (openid != null) {
            System.out.println("从缓存获取openid：" + openid);
            return openid;
        }

        // 2. 缓存未命中，调用微信API
        String url = String.format(
                "https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code",
                appId, appSecret, code);

        RestTemplate restTemplate = new RestTemplate();
        String body = restTemplate.getForObject(url, String.class);

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> map = mapper.readValue(body, Map.class);

        Object errcode = map.get("errcode");
        if (errcode != null && !errcode.toString().equals("0")) {
            throw new RuntimeException("微信API错误: " + map.get("errmsg"));
        }

        openid = (String) map.get("openid");
        if (openid == null) {
            throw new RuntimeException("返回无 openid");
        }

        // 3. 写入Redis缓存（5分钟过期）
        redisTemplate.opsForValue().set(cacheKey, openid, Duration.ofMinutes(5));
        System.out.println("openid写入缓存成功，key：" + cacheKey);

        return openid;
    }

    /**
     * 获取prepay_id
     * <p>
     * 调用微信支付统一下单接口，获取预支付交易会话标识
     * 该接口负责构建请求体、生成签名、发送请求和解析响应
     * </p>
     *
     * @param openid 用户openid
     * @param amount 支付金额（单位：元）
     * @param outTradeNo 商户订单号
     * @param description 订单描述
     * @return 预支付交易会话标识prepay_id
     */
    private String getPrepayId(String openid, String amount, String outTradeNo, String description) throws Exception {
        // 构建请求体
        String requestBody = buildRequestBody(openid, amount, outTradeNo, description);

        // 生成签名并构建Authorization头
        String authorization = buildAuthorization(requestBody);

        // 发送请求获取prepay_id
        String response = sendRequest(WX_API_URL, "POST", authorization, requestBody);

        // 解析prepay_id
        String prepayId = parsePrepayId(response);
        if (prepayId == null) {
            throw new RuntimeException("获取prepay_id失败: " + response);
        }

        return prepayId;
    }

    /**
     * 生成支付参数
     * <p>
     * 生成小程序前端调用wx.requestPayment所需的支付参数
     * 包括时间戳、随机字符串、签名等核心参数
     * </p>
     *
     * @param prepayId 预支付交易会话标识
     * @return 小程序支付参数
     */
    private Map<String, String> generatePaymentParams(String prepayId) throws Exception {
        String timeStamp = String.valueOf(Instant.now().getEpochSecond());
        String nonceStr = generateNonceStr();
        String packageValue = "prepay_id=" + prepayId;
        String signMessage = String.format("%s\n%s\n%s\n%s\n", appId, timeStamp, nonceStr, packageValue);
        String privateKey = loadPrivateKeyFromPem();
        String paySign = sign(signMessage, privateKey);

        Map<String, String> result = new HashMap<>();
        result.put("timeStamp", timeStamp);
        result.put("nonceStr", nonceStr);
        result.put("package", packageValue);
        result.put("paySign", paySign);
        result.put("appId", appId);
        result.put("signType", "RSA");

        System.out.println("直播人员支付参数生成成功");
        return result;
    }

    /**
     * 处理支付通知
     * <p>
     * 处理微信支付成功后的异步通知
     * 验证通知的真实性、处理订单状态更新
     * </p>
     *
     * @param notifyData 微信支付通知数据
     */
    public void handlePayNotify(String notifyData) throws Exception {
        System.out.println("开始处理直播人员支付通知：" + notifyData);

        // 验证通知签名（此处省略签名验证逻辑）
        // TODO: 实现微信支付通知签名验证

        // 解析通知数据
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> notifyMap = mapper.readValue(notifyData, Map.class);

        // 解密业务数据
        Map<String, Object> resource = (Map<String, Object>) notifyMap.get("resource");
        if (resource != null) {
            String ciphertext = (String) resource.get("ciphertext");
            String associated = (String) resource.get("associated_data");
            String nonce = (String) resource.get("nonce");

            // 解密
            String plainJson = com.ruoyi.mall.pay.service.utils.WXPayUtility.decrypt(ciphertext, associated, nonce, APIv3Key);
            System.out.println("解密后的业务数据：" + plainJson);

            // 解析解密后的数据
            Map<String, Object> plainMap = mapper.readValue(plainJson, Map.class);

            // 提取核心信息
            String transactionId = (String) plainMap.get("transaction_id");
            String outTradeNo = (String) plainMap.get("out_trade_no");
            Map<String, Object> amount = (Map<String, Object>) plainMap.get("amount");
            String total = String.valueOf(amount.get("total"));

            System.out.println("支付成功 - 微信订单号：" + transactionId +
                    "，商户订单号：" + outTradeNo + "，支付金额：" + total);

            // 更新直播人员订单状态为已支付
            if (outTradeNo.startsWith("LP")) {
                QueryWrapper<LivePersonOrder> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("order_no", outTradeNo);
                
                if (livePersonOrderService instanceof IService) {
                    IService<LivePersonOrder> service = (IService<LivePersonOrder>) livePersonOrderService;
                    LivePersonOrder livePersonOrder = service.getOne(queryWrapper);
                    
                    if (livePersonOrder != null) {
                        livePersonOrder.setStatus(1); // 1已付款
                        livePersonOrder.setTransactionId(transactionId);
                        livePersonOrder.setPayTime(LocalDateTime.now());
                        service.updateById(livePersonOrder);
                        System.out.println("直播人员订单状态更新成功：" + outTradeNo);
                    }
                }
            }
        } else {
            System.out.println("直播人员支付通知处理失败：缺少resource字段");
            throw new IllegalArgumentException("缺少resource字段");
        }
    }

    /**
     * 查询订单
     * <p>
     * 根据商户订单号或微信订单号查询订单状态和详情
     * 支持通过out_trade_no或transaction_id查询
     * </p>
     *
     * @param outTradeNo 商户订单号
     * @param transactionId 微信订单号
     * @return 订单查询结果
     */
    public Map<String, Object> queryOrder(String outTradeNo, String transactionId) throws Exception {
        String queryUrl;
        if (transactionId != null && !transactionId.isEmpty()) {
            // 通过微信订单号查询
            queryUrl = "https://api.mch.weixin.qq.com/v3/pay/transactions/id/" + transactionId + "?mchid=" + merchantId;
        } else {
            // 通过商户订单号查询
            queryUrl = String.format(WX_QUERY_URL, outTradeNo) + "?mchid=" + merchantId;
        }

        String authorization = buildAuthorization("");
        String response = sendRequest(queryUrl, "GET", authorization, null);

        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(response, Map.class);
    }

    /**
     * 订单退款
     * <p>
     * 处理订单退款请求，支持全额退款和部分退款
     * </p>
     *
     * @param outTradeNo 商户订单号
     * @param transactionId 微信订单号
     * @param outRefundNo 退款单号
     * @param amount 退款金额（单位：元）
     * @return 退款结果
     */
    public Map<String, Object> refundOrder(String outTradeNo, String transactionId, String outRefundNo, String amount) throws Exception {
        int refundAmount = new BigDecimal(amount).multiply(BigDecimal.valueOf(100)).intValue();
        int totalAmount = refundAmount; // 假设全额退款

        String requestBody = "{" +
                "\"out_trade_no\":\"" + outTradeNo + "\"," +
                "\"out_refund_no\":\"" + outRefundNo + "\"," +
                "\"reason\":\"直播服务退款\"," +
                "\"notify_url\":\"" + notifyUrl + "\"," +
                "\"amount\":{\"refund\":" + refundAmount + ",\"total\":" + totalAmount + ",\"currency\":\"CNY\"}" +
                "}";

        String authorization = buildAuthorization(requestBody);
        String response = sendRequest(WX_REFUND_URL, "POST", authorization, requestBody);

        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(response, Map.class);
    }

    // -------------------- 私有辅助方法 --------------------

    /**
     * 构建微信支付请求体
     */
    private String buildRequestBody(String openid, String amount, String outTradeNo, String description) {
        int totalCent = new BigDecimal(amount).multiply(BigDecimal.valueOf(100)).intValue();
        System.out.println("支付金额（元）：" + amount + "，转换为分：" + totalCent);

        return "{" +
                "\"appid\":\"" + appId + "\"," +
                "\"mchid\":\"" + merchantId + "\"," +
                "\"description\":\"" + description + "\"," +
                "\"out_trade_no\":\"" + outTradeNo + "\"," +
                "\"time_expire\":\"" + getExpireTime() + "\"," +
                "\"notify_url\":\"" + notifyUrl + "\"," +
                "\"amount\":{\"total\":" + totalCent + ",\"currency\":\"CNY\"}," +
                "\"payer\":{\"openid\":\"" + openid + "\"}" +
                "}";
    }

    /**
     * 获取过期时间
     */
    private String getExpireTime() {
        return DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(
                Instant.now().plusSeconds(900).atZone(ZoneId.of("+08:00"))
        );
    }

    /**
     * 构建Authorization头
     */
    private String buildAuthorization(String body) throws Exception {
        String method = "POST";
        String urlPath = "/v3/pay/transactions/jsapi";
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        String nonceStr = generateNonce();
        String message = buildSignMessage(method, urlPath, timestamp, nonceStr, body);
        PrivateKey privateKey = loadPrivateKey();
        String signature = sign(privateKey, message);

        return "WECHATPAY2-SHA256-RSA2048 " +
                "mchid=\"" + merchantId + "\"," +
                "nonce_str=\"" + nonceStr + "\"," +
                "timestamp=\"" + timestamp + "\"," +
                "serial_no=\"" + certSerialNo + "\"," +
                "signature=\"" + signature + "\"";
    }

    /**
     * 解析prepay_id
     */
    private String parsePrepayId(String response) {
        if (response.contains("prepay_id")) {
            int start = response.indexOf("\"prepay_id\":\"") + 13;
            int end = response.indexOf("\"", start);
            return response.substring(start, end);
        }
        return null;
    }

    /**
     * 发送HTTP请求
     */
    private String sendRequest(String urlStr, String method, String authorization, String body) throws IOException {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(method);
            conn.setRequestProperty("Authorization", authorization);
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");

            if ("POST".equals(method) && body != null) {
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);
                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = body.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }
            }

            int status = conn.getResponseCode();
            if (status == HttpURLConnection.HTTP_OK) {
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                    return br.lines().collect(Collectors.joining());
                }
            } else {
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8))) {
                    throw new IOException("HTTP error: " + status + "\n" + br.lines().collect(Collectors.joining()));
                }
            }
        } finally {
            if (conn != null) conn.disconnect();
        }
    }

    /**
     * 生成随机字符串（用于签名）
     */
    private String generateNonce() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 生成随机字符串（用于支付参数）
     */
    private String generateNonceStr() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 32);
    }

    /**
     * 构建签名信息
     */
    private String buildSignMessage(String method, String urlPath, String timestamp, String nonce, String body) {
        return method + "\n" + urlPath + "\n" + timestamp + "\n" + nonce + "\n" + body + "\n";
    }

    /**
     * 加载私钥（从classpath）
     */
    private PrivateKey loadPrivateKey() throws Exception {
        try (InputStream is = getClass().getClassLoader()
                .getResourceAsStream(PRIVATE_KEY_PATH)) {
            if (is == null) {
                throw new FileNotFoundException("未找到证书文件: " + PRIVATE_KEY_PATH);
            }

            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            byte[] data = new byte[1024];
            int n;
            while ((n = is.read(data)) != -1) {
                buffer.write(data, 0, n);
            }

            String content = buffer.toString(StandardCharsets.UTF_8.name())
                    .replaceAll("-----BEGIN PRIVATE KEY-----", "")
                    .replaceAll("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s", "");

            byte[] keyBytes = java.util.Base64.getDecoder().decode(content);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(keySpec);
        }
    }

    /**
     * 加载私钥（从classpath，返回字符串）
     */
    private String loadPrivateKeyFromPem() throws IOException {
        try (InputStream inputStream = new ClassPathResource(PRIVATE_KEY_PATH).getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            return reader.lines()
                    .filter(line -> !line.startsWith("-----BEGIN PRIVATE KEY-----"))
                    .filter(line -> !line.startsWith("-----END PRIVATE KEY-----"))
                    .collect(Collectors.joining())
                    .trim();
        }
    }

    /**
     * RSA签名方法（字符串私钥）
     */
    private String sign(String message, String privateKey) throws Exception {
        byte[] keyBytes = Base64.decodeBase64(privateKey);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey priKey = keyFactory.generatePrivate(keySpec);

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(priKey);
        signature.update(message.getBytes(StandardCharsets.UTF_8));
        return Base64.encodeBase64String(signature.sign());
    }

    /**
     * RSA签名方法（PrivateKey对象）
     */
    private String sign(PrivateKey privateKey, String message) throws Exception {
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(message.getBytes(StandardCharsets.UTF_8));
        return Base64.encodeBase64String(signature.sign());
    }
}
