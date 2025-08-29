package com.ruoyi.mall.pay.controller;

import com.ruoyi.mall.pay.service.WeChatPayService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;


import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/wechat/pay")
public class WeChatPayController {

    @Autowired
    private WeChatPayService weChatPayService;

    @PostMapping("/unified2")
    public ResponseEntity<Map<String, String>> unifiedPayment2(@RequestBody Map<String, Object> request) {
        try {
            // 1. 必填参数校验
            if (!request.containsKey("code") || !request.containsKey("amount") || !request.containsKey("out_trade_no")) {
                return ResponseEntity.badRequest()
                        .body(Collections.singletonMap("error", "缺少必填参数 code 或 amount"));
            }
            System.out.println("request==="+request);
            String code = String.valueOf(request.get("code"));
            String amount = String.valueOf(request.get("amount"));
            String outTradeNo = String.valueOf(request.get("out_trade_no"));
            String description = String.valueOf(request.getOrDefault("description", ""));
            System.out.println("request==="+code + "amount==="+amount + "description==="+description);
            /* 3. 业务调用 */
            Map<String, String> paymentParams = weChatPayService.getPaymentParams(code, amount, outTradeNo,description);
            return ResponseEntity.ok(paymentParams);
        } catch (Exception e) {
            /* 4. 异常返回 */
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }
}















/*

@RestController
@RequestMapping("/api/wechat/pay")
public class WeChatPayController {

    private static final String APP_ID = "wx2421b1c4370ec43b"; // 替换为你的小程序AppID
    private static final String PRIVATE_KEY_PATH = "cert/apiclient_key.pem"; // 私钥文件路径

    @GetMapping("/signature")
    public Map<String, String> generatePaymentSignature() throws Exception {
        // 1. 准备参数
        String timeStamp = String.valueOf(Instant.now().getEpochSecond());
        String nonceStr = generateNonceStr();
        String prepayId = "prepay_id=wx201410272009395522657a690389285100"; // 实际应从下单接口获取

        // 2. 构造签名串
        String signMessage = buildSignMessage(APP_ID, timeStamp, nonceStr, prepayId);

        // 3. 从文件加载私钥并计算签名
        String privateKey = loadPrivateKeyFromPem();
        String paySign = sign(signMessage, privateKey);

        // 4. 返回结果
        Map<String, String> result = new HashMap<>();
        result.put("timeStamp", timeStamp);
        result.put("nonceStr", nonceStr);
        result.put("package", prepayId);
        result.put("paySign", paySign);
        result.put("appId", APP_ID);
        result.put("signType", "RSA");

        return result;
    }

    private String generateNonceStr() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 32);
    }

    private String buildSignMessage(String appId, String timeStamp, String nonceStr, String prepayId) {
        return String.format("%s\n%s\n%s\n%s\n", appId, timeStamp, nonceStr, prepayId);
    }

    private String loadPrivateKeyFromPem() throws IOException {
        try (InputStream inputStream = new ClassPathResource(PRIVATE_KEY_PATH).getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            // 读取PEM文件内容，去除头尾标记和换行符
            String privateKeyPem = reader.lines()
                    .filter(line -> !line.startsWith("-----BEGIN PRIVATE KEY-----"))
                    .filter(line -> !line.startsWith("-----END PRIVATE KEY-----"))
                    .collect(Collectors.joining());

            return privateKeyPem.trim();
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
}*/
