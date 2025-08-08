package com.ruoyi.mall.pay;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;

import javax.net.ssl.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.*;

/**
 * 微信 V3 JSAPI 下单示例
 */
public class WXpay {

    public static void main(String[] args) throws Exception {
        // 1. 构造请求体
        Map<String, Object> body = buildBody();
        String jsonBody = new ObjectMapper().writeValueAsString(body);

        // 2. 生成微信 V3 Authorization 头
        String url = "https://api.mch.weixin.qq.com/v3/pay/transactions/jsapi";
        String authorization = buildAuthorization("POST", "/v3/pay/transactions/jsapi", jsonBody);

        // 3. 发送 HTTPS 请求
        String resp = post(url, jsonBody, authorization);
        System.out.println("微信返回：\n" + resp);
    }

    /* -------------------- 构造请求体 -------------------- */
    private static Map<String, Object> buildBody() {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("appid", "wxd678efh567hg6787");
        body.put("mchid", "1230000109");
        body.put("description", "Image形象店-深圳腾大-QQ公仔");
        body.put("out_trade_no", "1217752501201407033233368018");
        body.put("time_expire", "2018-06-08T10:34:56+08:00");
        body.put("attach", "自定义数据说明");
        body.put("notify_url", "https://www.weixin.qq.com/wxpay/pay.php");
        body.put("goods_tag", "WXG");
        body.put("support_fapiao", false);

        Map<String, Object> amount = new LinkedHashMap<>();
        amount.put("total", 100);
        amount.put("currency", "CNY");
        body.put("amount", amount);

        Map<String, Object> payer = new LinkedHashMap<>();
        payer.put("openid", "ovqdowRIfstpQK_kYShFS2MSS9XS");
        body.put("payer", payer);

        Map<String, Object> detail = new LinkedHashMap<>();
        detail.put("cost_price", 608800);
        detail.put("invoice_id", "微信123");

        List<Map<String, Object>> goodsDetail = new ArrayList<>();
        Map<String, Object> good = new LinkedHashMap<>();
        good.put("merchant_goods_id", "1246464644");
        good.put("wechatpay_goods_id", "1001");
        good.put("goods_name", "iPhoneX 256G");
        good.put("quantity", 1);
        good.put("unit_price", 528800);
        goodsDetail.add(good);
        detail.put("goods_detail", goodsDetail);
        body.put("detail", detail);

        Map<String, Object> scene = new LinkedHashMap<>();
        scene.put("payer_client_ip", "14.23.150.211");
        scene.put("device_id", "013467007045764");

        Map<String, Object> store = new LinkedHashMap<>();
        store.put("id", "0001");
        store.put("name", "腾讯大厦分店");
        store.put("area_code", "440305");
        store.put("address", "广东省深圳市南山区科技中一道10000号");
        scene.put("store_info", store);
        body.put("scene_info", scene);

        Map<String, Object> settle = new LinkedHashMap<>();
        settle.put("profit_sharing", false);
        body.put("settle_info", settle);

        return body;
    }

    /* -------------------- 生成 Authorization 头 -------------------- */
    private static String buildAuthorization(String method, String path, String body) throws Exception {
        String mchid = "1723914419";
        String serialNo = "YOUR_CERT_SERIAL_NO";          // ← 换成自己的
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        String nonceStr = UUID.randomUUID().toString().replace("-", "");

        String message = method + "\n" + path + "\n" + timestamp + "\n" + nonceStr + "\n" + body + "\n";
        String signature = sign(message);
        return "WECHATPAY2-SHA256-RSA2048 " +
                "mchid=\"" + mchid + "\"," +
                "nonce_str=\"" + nonceStr + "\"," +
                "signature=\"" + signature + "\"," +
                "timestamp=\"" + timestamp + "\"," +
                "serial_no=\"" + serialNo + "\"";
    }

    private static String sign(String message) throws Exception {
        PrivateKey merchantKey = loadPrivateKey();
        Signature sign = Signature.getInstance("SHA256withRSA");
        sign.initSign(merchantKey);
        sign.update(message.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(sign.sign());
    }

    /* -------------------- 读取私钥 -------------------- */
    private static PrivateKey loadPrivateKey() throws Exception {
        InputStream is = new ClassPathResource("apiclient_key.pem").getInputStream();
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int n;
        while ((n = is.read(data)) != -1) {
            buffer.write(data, 0, n);
        }
        is.close();
        String content = buffer.toString(StandardCharsets.UTF_8.name())
                .replaceAll("-----BEGIN PRIVATE KEY-----", "")
                .replaceAll("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");
        byte[] keyBytes = Base64.getDecoder().decode(content);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(new PKCS8EncodedKeySpec(keyBytes));
    }

    /* -------------------- 发送 HTTPS 请求 -------------------- */
    private static String post(String urlStr, String jsonBody, String authorization) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Authorization", authorization);
        conn.setDoOutput(true);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(jsonBody.getBytes(StandardCharsets.UTF_8));
        }

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) sb.append(line);
            return sb.toString();
        }
    }
}