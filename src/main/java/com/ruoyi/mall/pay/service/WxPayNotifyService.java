package com.ruoyi.mall.pay.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.mall.order.domain.MallOrder;
import com.ruoyi.mall.order.service.impl.MallOrderServiceImpl;
import com.ruoyi.mall.pay.service.utils.WXPayUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.time.LocalDateTime;
import java.util.Map;

/*
 * 验签 + 解密 + 业务处理类
 * */
@Slf4j
@Service
@RequiredArgsConstructor
public class  WxPayNotifyService {

    private final ObjectMapper mapper = new ObjectMapper();
    @Value("${wechat.pay.APIv3_key}")
    private String APIv3Key;

    // 商户配置  ： 商户号、证书序列号、私钥、公钥
    @Value("${wechat.cert.pub_wechatpay}")
    private String pub_wechatpay;
    @Value("${wechat.cert.pub_key}")
    private String pub_key;
    @Value("${wechat.cert.apiclient_key}")
    private String apiclient_key;

//    private static final String PRIVATE_KEY_PATH = "classpath:cert/apiclient_key.pem";
    private final MallOrderServiceImpl mallOrderServiceImpl;

    public String handle(String body, Map<String, String> headers) {
        try {
            // 1. 验签
            if (!verifySignature(body, headers)) {
                log.warn("验签失败");
                return "fail";
            }
            /*// 2. 解密
            Map<String, Object> map = mapper.readValue(body, Map.class);
            String resourceStr = (String) ((Map<String, Object>) map.get("resource")).get("ciphertext");
            log.warn("解密resourceStr"+resourceStr);
            byte[] plain = WXPayUtility.decrypt(resourceStr, WXPayUtility.loadPrivateKeyFromPath(apiclient_key));
            String plainJson = new String(plain, StandardCharsets.UTF_8);
            log.info("解密后：{}", plainJson);*/

            // 2. 解密
            Map<String, Object> map = mapper.readValue(body, Map.class);
            Map<String, Object> resource = (Map<String, Object>) map.get("resource");
            String ciphertext   = (String) resource.get("ciphertext");
            String associated   = (String) resource.get("associated_data");
            String nonce        = (String) resource.get("nonce");

            String plainJson = WXPayUtility.decrypt(ciphertext, associated, nonce, APIv3Key);
            log.info("解密后：{}", plainJson);

            // 3. 业务处理（幂等）
            return processBusiness(plainJson);
        } catch (Exception e) {
            log.error("回调处理异常", e);
            return "fail";
        }
    }

    /* ---------- 私有辅助方法 ---------- */
    private boolean verifySignature(String body, Map<String, String> headers) throws Exception {
        String serial    = headers.get("wechatpay-serial");
        String signature = headers.get("wechatpay-signature");
        String timestamp = headers.get("wechatpay-timestamp");
        String nonce     = headers.get("wechatpay-nonce");
        log.warn("timestamp：{}", timestamp);
        String signStr = timestamp + "\n" + nonce + "\n" + body + "\n";
        log.warn("signStr：{}", signStr);
        log.warn("pub_wechatpay：{}", pub_wechatpay);
        PublicKey key = serial.startsWith("PUB_KEY_ID_") ?
                WXPayUtility.loadPublicKeyFromPath(pub_key) :
                WXPayUtility.loadPublicWechatpayPath(pub_wechatpay);
        return WXPayUtility.verify(signature, signStr, key);
    }

    private String processBusiness(String plainJson) throws Exception {
        Map<String, Object> map = mapper.readValue(plainJson, Map.class);
        String outTradeNo = (String) map.get("out_trade_no");
        String tradeState = (String) map.get("trade_state");
        String transactionId = (String) map.get("transaction_id");
        log.warn("回调map：{}", map);
        if (!"SUCCESS".equals(tradeState)) {
            log.warn("非成功回调：{}", tradeState);
            return "fail";
        }

        // 幂等：已处理过直接返回
        MallOrder order = mallOrderServiceImpl.lambdaQuery()
                .eq(MallOrder::getOrderNo, outTradeNo)
                .eq(MallOrder::getDeleted, 0)
                .one();
        if (order == null) return "fail";
        if ("1".equals(order.getStatus())) return "success";

        // 更新订单
        order.setStatus(1);
        order.setTransactionId(transactionId);
        order.setPayTime(LocalDateTime.now());
        log.warn("更新订单order：{}", order);
        mallOrderServiceImpl.updateById(order);

        // 记录流水
        /*paymentLogService.save(PaymentLog.builder()
                .orderNo(outTradeNo)
                .transactionId(transactionId)
                .amount(order.getTotalAmount())
                .payTime(order.getPayTime())
                .build());

        // 后续业务（发货、库存、积分等）
        orderService.handleAfterPay(order);*/

        return "success";
    }
}
