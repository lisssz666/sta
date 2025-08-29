package com.ruoyi.mall.pay.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.mall.pay.WechatPayJSAPI;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/*
* 返回支付参数类
* */
@Service
public class WeChatPayService {

    @Value("${wechat.appid}")
    private String appId;

    @Value("${wechat.secret}")
    private String appSecret;

    @Value("${wechat.pay.mchid}")
    private String merchantId;

    @Value("${wechat.pay.serial_no}")
    private String certSerialNo;

    @Value("${wechat.pay.notify_url}")
    private String notifyUrl;

    private static final String PRIVATE_KEY_PATH = "cert/apiclient_key.pem";
    private static final String WX_API_URL = "https://api.mch.weixin.qq.com/v3/pay/transactions/jsapi";

    @Autowired
    private RedisTemplate<String, String> redisTemplate;


    /**
     * 通过code获取支付参数
     */
    public Map<String, String> getPaymentParams(String code, String amount,String outTradeNo, String description) throws Exception {
        // 1. 获取openid
        String openid = getOpenidByCode(code);
        System.out.println("openid==="+openid);
        // 2. 获取prepay_id
        String prepayId = getPrepayId(openid, amount, outTradeNo, description);
        System.out.println("prepayId==="+prepayId);
        // 3. 生成支付参数
        return generatePaymentParams(prepayId);
    }

    /**
     * 通过code获取openid
     */
    private String getOpenidByCode(String code) throws Exception {
        // 1. 先看缓存
        String cacheKey = "wx:code:" + code;
        String openid = redisTemplate.opsForValue().get(cacheKey);
        if (openid != null) {
            return openid;
        }
        // 2. 缓存未命中，调微信
        String url = String.format(
                "https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code",
                appId, appSecret, code);

        RestTemplate restTemplate = new RestTemplate();
        String body = restTemplate.getForObject(url, String.class);

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> map = mapper.readValue(body, Map.class);

        Object errcode = map.get("errcode");
        if (errcode != null && !errcode.toString().equals("0")) {
            throw new RuntimeException("微信错误: " + map.get("errmsg"));
        }

        openid = (String) map.get("openid");
        if (openid == null) {
            throw new RuntimeException("返回无 openid");
        }
        // 3. 写缓存（5 分钟过期）
        redisTemplate.opsForValue().set(cacheKey, openid, Duration.ofMinutes(5));
        return openid;
    }

    /**
     * 获取prepay_id
     */
    private String getPrepayId(String openid, String amount,String outTradeNo, String description) throws Exception {
        // 构建请求体
        String requestBody = buildRequestBody(openid, amount, outTradeNo,description);

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
        return result;
    }

    // -------------------- 私有辅助方法 --------------------

    private String buildRequestBody(String openid, String amount,String outTradeNo, String description) {
        int totalCent = new BigDecimal(amount).multiply(BigDecimal.valueOf(100)).intValue();
//        String outTradeNo = "ORDER" + System.currentTimeMillis();
        System.out.println("amount==="+amount);
        System.out.println("totalCent==="+totalCent);

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

    private String getExpireTime() {
        return DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(
                Instant.now().plusSeconds(900).atZone(ZoneId.of("+08:00"))
        );
    }

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

    private String parsePrepayId(String response) {
        if (response.contains("prepay_id")) {
            int start = response.indexOf("\"prepay_id\":\"") + 13;
            int end = response.indexOf("\"", start);
            return response.substring(start, end);
        }
        return null;
    }

    private String sendRequest(String urlStr, String method, String authorization, String body) throws IOException {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(method);
            conn.setRequestProperty("Authorization", authorization);
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = body.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
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

    private String generateNonce() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    private String generateNonceStr() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 32);
    }

    private String buildSignMessage(String method, String urlPath, String timestamp, String nonce, String body) {
        return method + "\n" + urlPath + "\n" + timestamp + "\n" + nonce + "\n" + body + "\n";
    }

    private PrivateKey loadPrivateKey() throws Exception {
        // 从classpath读取PEM文件
        try (InputStream is = WechatPayJSAPI.class.getClassLoader()
                .getResourceAsStream(PRIVATE_KEY_PATH)) { //测试
//                .getResourceAsStream("cert/apiclient_key.pem")) {
            if (is == null) {
                throw new FileNotFoundException("未找到证书文件: cert/apiclient_key.pem");
            }

            // 读取PEM文件内容
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            byte[] data = new byte[1024];
            int n;
            while ((n = is.read(data)) != -1) {
                buffer.write(data, 0, n);
            }

            // 处理PEM格式
            String content = buffer.toString(StandardCharsets.UTF_8.name())
                    .replaceAll("-----BEGIN PRIVATE KEY-----", "")
                    .replaceAll("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s", "");

            // 解码Base64并生成私钥
            byte[] keyBytes = java.util.Base64.getDecoder().decode(content);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(keySpec);
        }
    }

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

    private String sign(String message, String privateKey) throws Exception {
        // 将Base64编码的私钥字符串转换为字节数组
        byte[] keyBytes = Base64.decodeBase64(privateKey);

        // 创建PKCS8EncodedKeySpec对象
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);

        // 获取RSA KeyFactory实例
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        // 生成私钥对象
        PrivateKey key = keyFactory.generatePrivate(keySpec);

        // 创建Signature实例并初始化
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(key);
        signature.update(message.getBytes(StandardCharsets.UTF_8));

        // 生成签名并Base64编码
        byte[] signBytes = signature.sign();
        return Base64.encodeBase64String(signBytes);
    }

    private String sign(PrivateKey privateKey, String message) throws Exception {
        // 使用SHA256withRSA算法签名
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(message.getBytes(StandardCharsets.UTF_8));
        // 生成签名并Base64编码
        byte[] signBytes = signature.sign();
        return java.util.Base64.getEncoder().encodeToString(signBytes);
    }
}