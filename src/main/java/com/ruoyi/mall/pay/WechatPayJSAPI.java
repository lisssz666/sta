package com.ruoyi.mall.pay;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.UUID;

public class WechatPayJSAPI {

    // 配置参数 - 替换为你的实际值
    private static final String MERCHANT_ID = "1723914419";
    private static final String APP_ID = "wx6a9f44e2b18a316e";
    private static final String CERT_SERIAL_NO = "3E3D061F8B9979DE74A58AEADCBD25D6358FFCE8"; // 证书序列号(商户平台获取)
    private static final String OPENID = "oOYZb7ECLnxwKqmQgXhIFxO68-30";
    private static final String NOTIFY_URL = "https://www.ybyoulan.cn:8088/order/notify";
    private static final String API_URL = "https://api.mch.weixin.qq.com/v3/pay/transactions/jsapi";

    public static void main(String[] args) {
        try {
            // 1. 准备请求数据
            String requestBody = buildRequestBody();
            System.out.println("请求体: " + requestBody);

            // 2. 生成签名并构建Authorization头
            String authorization = buildAuthorization(requestBody);
            System.out.println("Authorization: " + authorization);

            // 3. 发送请求
            String response = sendRequest(API_URL, "POST", authorization, requestBody);
            System.out.println("响应结果: " + response);

            // 4. 解析prepay_id
            if (response.contains("prepay_id")) {
                int start = response.indexOf("\"prepay_id\":\"") + 13;
                int end = response.indexOf("\"", start);
                String prepayId = response.substring(start, end);
                System.out.println("\n获取到的prepay_id: " + prepayId);
            } else {
                System.err.println("未找到prepay_id: " + response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String buildRequestBody() {
        // 生成唯一订单号
        String outTradeNo = "ORDER" + System.currentTimeMillis();

        // 构建JSON请求体
        return "{"
                + "\"appid\":\"" + APP_ID + "\","
                + "\"mchid\":\"" + MERCHANT_ID + "\","
                + "\"description\":\"测试商品\","
                + "\"out_trade_no\":\"" + outTradeNo + "\","
                + "\"time_expire\":\"" + getExpireTime() + "\","
                + "\"notify_url\":\"" + NOTIFY_URL + "\","
                + "\"amount\":{"
                + "\"total\":100,"
                + "\"currency\":\"CNY\""
                + "},"
                + "\"payer\":{"
                + "\"openid\":\"" +OPENID +"\"" // 替换为实际用户的openid
                + "}"
                + "}";
    }

    private static String getExpireTime() {
        // 生成15分钟后过期的时间
        return DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(
                Instant.now().plusSeconds(900).atZone(ZoneId.of("+08:00"))
        );
    }

    private static String buildAuthorization(String body) throws Exception {
        // 1. 准备签名要素
        String method = "POST";
        String urlPath = "/v3/pay/transactions/jsapi";
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        String nonceStr = generateNonce();

        // 2. 构建签名字符串
        String message = buildSignMessage(method, urlPath, timestamp, nonceStr, body);

        // 3. 加载私钥
        PrivateKey privateKey = loadPrivateKey();

        // 4. 生成签名
        String signature = sign(privateKey, message);

        // 5. 构建Authorization头
        return "WECHATPAY2-SHA256-RSA2048 " +
                "mchid=\"" + MERCHANT_ID + "\"," +
                "nonce_str=\"" + nonceStr + "\"," +
                "timestamp=\"" + timestamp + "\"," +
                "serial_no=\"" + CERT_SERIAL_NO + "\"," +
                "signature=\"" + signature + "\"";
    }

    private static String buildSignMessage(String method, String urlPath, String timestamp, String nonce, String body) {
        return method + "\n" +
                urlPath + "\n" +
                timestamp + "\n" +
                nonce + "\n" +
                body + "\n";
    }

    private static String generateNonce() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    private static String sendRequest(String urlStr, String method, String authorization, String body) throws IOException {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();

            // 设置请求属性
            conn.setRequestMethod(method);
            conn.setRequestProperty("Authorization", authorization);
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");
            conn.setDoOutput(true);

            // 发送请求体
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = body.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // 获取响应
            int status = conn.getResponseCode();
            if (status == HttpURLConnection.HTTP_OK) {
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    return response.toString();
                }
            } else {
                // 读取错误流
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8))) {
                    StringBuilder errorResponse = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        errorResponse.append(line);
                    }
                    throw new IOException("HTTP error: " + status + "\n" + errorResponse);
                }
            }
        } finally {
            if (conn != null) conn.disconnect();
        }
    }

    // -------------------- 私钥加载方法 --------------------
    private static PrivateKey loadPrivateKey() throws Exception {
        // 从classpath读取PEM文件
        try (InputStream is = WechatPayJSAPI.class.getClassLoader()
                .getResourceAsStream("cert/apiclient_key.pem")) {
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
            byte[] keyBytes = Base64.getDecoder().decode(content);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(keySpec);
        }
    }

    // -------------------- 签名方法 --------------------
    private static String sign(PrivateKey privateKey, String message) throws Exception {
        // 使用SHA256withRSA算法签名
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(message.getBytes(StandardCharsets.UTF_8));

        // 生成签名并Base64编码
        byte[] signBytes = signature.sign();
        return Base64.getEncoder().encodeToString(signBytes);
    }
}