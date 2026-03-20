package com.ruoyi.mall.pay.controller;

import com.ruoyi.mall.pay.service.RefereePayService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.Map;

/**
 * 裁判支付控制器
 * <p>
 * 裁判支付功能专门用于处理与裁判相关的支付场景，
 * 提供统一支付下单、支付回调处理、订单查询等核心功能
 * </p>
 * 
 * @author ruoyi
 * @version 1.0.0
 * @since 2026-01-14
 */
@RestController
@RequestMapping("/referee/pay")
public class RefereePayController {

    @Autowired
    private RefereePayService refereePayService;

    /**
     * 统一支付下单接口
     * <p>
     * 用于裁判支付场景的统一支付下单，支持微信小程序支付方式
     * 该接口负责参数校验、业务逻辑处理、调用支付服务获取支付参数
     * </p>
     *
     * @param request 支付请求参数，包含以下必填字段：
     *                - code：微信小程序登录凭证code（用于获取openid）
     *                - amount：支付金额（单位：元）
     *                - out_trade_no：商户订单号（唯一标识）
     *                - description：订单描述（可选）
     * @return 支付参数或错误信息
     */
    @PostMapping("/unified")
    public ResponseEntity<Map<String, String>> unifiedPayment(@RequestBody Map<String, Object> request) {
        try {
            // 1. 必填参数校验
            // 校验核心支付参数是否完整
            if (!request.containsKey("code") || !request.containsKey("amount") || !request.containsKey("out_trade_no")) {
                return ResponseEntity.badRequest()
                        .body(Collections.singletonMap("error", "缺少必填参数 code、amount 或 out_trade_no"));
            }

            // 2. 参数提取与转换
            String code = String.valueOf(request.get("code"));
            String amount = String.valueOf(request.get("amount"));
            String outTradeNo = String.valueOf(request.get("out_trade_no"));
            String description = String.valueOf(request.getOrDefault("description", "裁判服务费"));

            System.out.println("裁判支付请求参数：" + request);
            System.out.println("code：" + code + "，金额：" + amount + "，订单号：" + outTradeNo + "，描述：" + description);

            /* 3. 业务调用 */
            Map<String, String> paymentParams = refereePayService.getPaymentParams(code, amount, outTradeNo, description);

            // 4. 成功返回支付参数
            return ResponseEntity.ok(paymentParams);

        } catch (Exception e) {
            /* 4. 异常处理与返回 */
            System.err.println("裁判支付下单失败：" + e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    /**
     * 支付通知回调接口
     * <p>
     * 用于接收微信支付成功后的异步通知，处理支付结果
     * 该接口负责验证通知的真实性、处理订单状态更新、业务逻辑确认
     * </p>
     *
     * @param notifyData 微信支付通知数据
     * @return 处理结果
     */
    @PostMapping("/notify")
    public ResponseEntity<Map<String, String>> payNotify(@RequestBody String notifyData) {
        try {
            System.out.println("收到裁判支付通知：" + notifyData);
            refereePayService.handlePayNotify(notifyData);
            return ResponseEntity.ok(Collections.singletonMap("code", "SUCCESS"));
        } catch (Exception e) {
            System.err.println("裁判支付通知处理失败：" + e.getMessage());
            return ResponseEntity.ok(Collections.singletonMap("code", "FAIL"));
        }
    }

    /**
     * 订单查询接口
     * <p>
     * 用于查询裁判支付订单的状态和详情
     * 支持通过商户订单号或微信订单号进行查询
     * </p>
     *
     * @param request 查询请求参数，包含以下字段：
     *                - out_trade_no：商户订单号（必填）
     *                - transaction_id：微信订单号（可选）
     * @return 订单查询结果
     */
    @PostMapping("/query")
    public ResponseEntity<Map<String, Object>> queryOrder(@RequestBody Map<String, Object> request) {
        try {
            String outTradeNo = String.valueOf(request.get("out_trade_no"));
            String transactionId = String.valueOf(request.getOrDefault("transaction_id", ""));

            Map<String, Object> orderInfo = refereePayService.queryOrder(outTradeNo, transactionId);
            return ResponseEntity.ok(orderInfo);

        } catch (Exception e) {
            System.err.println("订单查询失败：" + e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    /**
     * 订单退款接口
     * <p>
     * 用于处理裁判支付订单的退款请求
     * 支持全额退款和部分退款
     * </p>
     *
     * @param request 退款请求参数，包含以下字段：
     *                - out_trade_no：商户订单号（必填）
     *                - transaction_id：微信订单号（可选）
     *                - out_refund_no：退款单号（必填）
     *                - amount：退款金额（单位：元，必填）
     * @return 退款结果
     */
    @PostMapping("/refund")
    public ResponseEntity<Map<String, Object>> refundOrder(@RequestBody Map<String, Object> request) {
        try {
            String outTradeNo = String.valueOf(request.get("out_trade_no"));
            String transactionId = String.valueOf(request.getOrDefault("transaction_id", ""));
            String outRefundNo = String.valueOf(request.get("out_refund_no"));
            String amount = String.valueOf(request.get("amount"));

            Map<String, Object> refundInfo = refereePayService.refundOrder(outTradeNo, transactionId, outRefundNo, amount);
            return ResponseEntity.ok(refundInfo);

        } catch (Exception e) {
            System.err.println("订单退款失败：" + e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }
}
