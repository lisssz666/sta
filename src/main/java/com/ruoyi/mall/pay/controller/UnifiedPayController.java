package com.ruoyi.mall.pay.controller;

import com.ruoyi.mall.pay.service.LivePersonPayService;
import com.ruoyi.mall.pay.service.RefereePayService;
import com.ruoyi.mall.pay.service.WeChatPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

/**
 * 统一支付控制器
 * <p>
 * 整合裁判支付、直播支付、商品支付三大支付场景，通过 payType 参数区分支付类型
 * 提供统一支付下单、支付回调处理、订单查询、订单退款等核心功能
 * </p>
 *
 * @author ruoyi
 * @version 1.0.0
 * @since 2026-05-13
 */
@RestController
@RequestMapping("/pay")
public class UnifiedPayController {

    /**
     * 支付类型常量定义
     */
    private static final String PAY_TYPE_REF = "REF";      // 裁判支付
    private static final String PAY_TYPE_LIVE = "LIVE";    // 直播支付
    private static final String PAY_TYPE_MALL = "MALL";    // 商品支付

    @Autowired
    private RefereePayService refereePayService;

    @Autowired
    private LivePersonPayService livePersonPayService;

    @Autowired
    private WeChatPayService weChatPayService;

    /**
     * 统一支付下单接口
     * <p>
     * 支持裁判支付、直播支付、商品支付三种场景，通过 payType 参数区分
     * </p>
     *
     * @param request 支付请求参数，包含以下字段：
     *                - payType：支付类型（必填）REF-裁判支付, LIVE-直播支付, MALL-商品支付
     *                - code：微信小程序登录凭证code（用于获取openid，必填）
     *                - amount：支付金额（单位：元，必填）
     *                - out_trade_no：商户订单号（唯一标识，必填）
     *                - description：订单描述（可选，不同支付类型有默认值）
     * @return 支付参数或错误信息
     */
    @PostMapping("/unified")
    public ResponseEntity<Map<String, String>> unifiedPayment(@RequestBody Map<String, Object> request) {
        try {
            // 1. 参数校验
            String error = validatePaymentRequest(request);
            if (StringUtils.hasText(error)) {
                return ResponseEntity.badRequest().body(Collections.singletonMap("error", error));
            }

            // 2. 参数提取
            String payType = String.valueOf(request.get("payType"));
            String code = String.valueOf(request.get("code"));
            String amount = String.valueOf(request.get("amount"));
            String outTradeNo = String.valueOf(request.get("out_trade_no"));
            String description = buildDescription(payType, request);

            System.out.println("统一支付请求 - 类型:" + payType + ", 订单号:" + outTradeNo + ", 金额:" + amount);

            // 3. 根据支付类型调用对应的支付服务
            Map<String, String> paymentParams;
            switch (payType) {
                case PAY_TYPE_REF:
                    paymentParams = refereePayService.getPaymentParams(code, amount, outTradeNo, description);
                    break;
                case PAY_TYPE_LIVE:
                    paymentParams = livePersonPayService.getPaymentParams(code, amount, outTradeNo, description);
                    break;
                case PAY_TYPE_MALL:
                    paymentParams = weChatPayService.getPaymentParams(code, amount, outTradeNo, description);
                    break;
                default:
                    throw new IllegalArgumentException("不支持的支付类型: " + payType);
            }

            // 4. 成功返回
            return ResponseEntity.ok(paymentParams);

        } catch (IllegalArgumentException e) {
            // 参数错误
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage()));
        } catch (Exception e) {
            // 系统错误
            System.err.println("支付下单失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    /**
     * 支付通知回调接口
     * <p>
     * 接收微信支付异步通知，根据订单号前缀判断支付类型并处理
     * </p>
     *
     * @param notifyData 微信支付通知数据
     * @return 处理结果
     */
    @PostMapping("/notify")
    public ResponseEntity<Map<String, String>> payNotify(@RequestBody String notifyData) {
        try {
            System.out.println("收到支付通知: " + notifyData);

            // 根据订单号前缀判断支付类型并处理
            String payType = parsePayTypeFromNotify(notifyData);
            switch (payType) {
                case PAY_TYPE_REF:
                    refereePayService.handlePayNotify(notifyData);
                    break;
                case PAY_TYPE_LIVE:
                    livePersonPayService.handlePayNotify(notifyData);
                    break;
                case PAY_TYPE_MALL:
                    weChatPayService.handlePayNotify(notifyData);
                    break;
                default:
                    throw new IllegalArgumentException("无法识别的支付类型");
            }

            return ResponseEntity.ok(Collections.singletonMap("code", "SUCCESS"));
        } catch (Exception e) {
            System.err.println("支付通知处理失败: " + e.getMessage());
            return ResponseEntity.ok(Collections.singletonMap("code", "FAIL"));
        }
    }

    /**
     * 订单查询接口
     * <p>
     * 支持通过商户订单号查询订单状态
     * </p>
     *
     * @param request 查询请求参数：
     *                - payType：支付类型（必填）
     *                - out_trade_no：商户订单号（必填）
     *                - transaction_id：微信订单号（可选）
     * @return 订单信息
     */
    @PostMapping("/query")
    public ResponseEntity<Map<String, Object>> queryOrder(@RequestBody Map<String, Object> request) {
        try {
            // 参数校验
            String payType = String.valueOf(request.get("payType"));
            String outTradeNo = String.valueOf(request.get("out_trade_no"));
            
            if (!validatePayType(payType)) {
                return ResponseEntity.badRequest().body(Collections.singletonMap("error", "不支持的支付类型"));
            }
            if (!StringUtils.hasText(outTradeNo)) {
                return ResponseEntity.badRequest().body(Collections.singletonMap("error", "缺少订单号"));
            }

            String transactionId = String.valueOf(request.getOrDefault("transaction_id", ""));

            // 根据支付类型查询
            Map<String, Object> orderInfo;
            switch (payType) {
                case PAY_TYPE_REF:
                    orderInfo = refereePayService.queryOrder(outTradeNo, transactionId);
                    break;
                case PAY_TYPE_LIVE:
                    orderInfo = livePersonPayService.queryOrder(outTradeNo, transactionId);
                    break;
                case PAY_TYPE_MALL:
                    orderInfo = weChatPayService.queryOrder(outTradeNo, transactionId);
                    break;
                default:
                    throw new IllegalArgumentException("不支持的支付类型");
            }

            return ResponseEntity.ok(orderInfo);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage()));
        } catch (Exception e) {
            System.err.println("订单查询失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    /**
     * 订单退款接口
     * <p>
     * 支持订单退款功能
     * </p>
     *
     * @param request 退款请求参数：
     *                - payType：支付类型（必填）
     *                - out_trade_no：商户订单号（必填）
     *                - out_refund_no：退款单号（必填）
     *                - amount：退款金额（单位：元，必填）
     *                - transaction_id：微信订单号（可选）
     * @return 退款结果
     */
    @PostMapping("/refund")
    public ResponseEntity<Map<String, Object>> refundOrder(@RequestBody Map<String, Object> request) {
        try {
            // 参数校验
            String payType = String.valueOf(request.get("payType"));
            String outTradeNo = String.valueOf(request.get("out_trade_no"));
            String outRefundNo = String.valueOf(request.get("out_refund_no"));
            String amount = String.valueOf(request.get("amount"));

            if (!validatePayType(payType)) {
                return ResponseEntity.badRequest().body(Collections.singletonMap("error", "不支持的支付类型"));
            }
            if (!StringUtils.hasText(outTradeNo)) {
                return ResponseEntity.badRequest().body(Collections.singletonMap("error", "缺少订单号"));
            }
            if (!StringUtils.hasText(outRefundNo)) {
                return ResponseEntity.badRequest().body(Collections.singletonMap("error", "缺少退款单号"));
            }
            if (!StringUtils.hasText(amount)) {
                return ResponseEntity.badRequest().body(Collections.singletonMap("error", "缺少退款金额"));
            }

            String transactionId = String.valueOf(request.getOrDefault("transaction_id", ""));

            // 根据支付类型退款
            Map<String, Object> refundInfo;
            switch (payType) {
                case PAY_TYPE_REF:
                    refundInfo = refereePayService.refundOrder(outTradeNo, transactionId, outRefundNo, amount);
                    break;
                case PAY_TYPE_LIVE:
                    refundInfo = livePersonPayService.refundOrder(outTradeNo, transactionId, outRefundNo, amount);
                    break;
                case PAY_TYPE_MALL:
                    refundInfo = weChatPayService.refundOrder(outTradeNo, transactionId, outRefundNo, amount);
                    break;
                default:
                    throw new IllegalArgumentException("不支持的支付类型");
            }

            return ResponseEntity.ok(refundInfo);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage()));
        } catch (Exception e) {
            System.err.println("订单退款失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    /**
     * 校验支付请求参数
     *
     * @param request 请求参数
     * @return 错误信息，如果校验通过返回空字符串
     */
    private String validatePaymentRequest(Map<String, Object> request) {
        // 校验支付类型
        if (!request.containsKey("payType")) {
            return "缺少支付类型参数 payType";
        }
        String payType = String.valueOf(request.get("payType"));
        if (!validatePayType(payType)) {
            return "不支持的支付类型: " + payType + "，支持的类型: REF(裁判), LIVE(直播), MALL(商品)";
        }

        // 校验核心支付参数
        if (!request.containsKey("code")) {
            return "缺少微信登录凭证 code";
        }
        if (!request.containsKey("amount")) {
            return "缺少支付金额 amount";
        }
        if (!request.containsKey("out_trade_no")) {
            return "缺少商户订单号 out_trade_no";
        }

        // 校验金额格式
        try {
            Double.parseDouble(String.valueOf(request.get("amount")));
        } catch (NumberFormatException e) {
            return "金额格式不正确";
        }

        return "";
    }

    /**
     * 校验支付类型是否合法
     *
     * @param payType 支付类型
     * @return 是否合法
     */
    private boolean validatePayType(String payType) {
        return PAY_TYPE_REF.equals(payType) || PAY_TYPE_LIVE.equals(payType) || PAY_TYPE_MALL.equals(payType);
    }

    /**
     * 根据支付类型构建订单描述
     *
     * @param payType  支付类型
     * @param request  请求参数
     * @return 订单描述
     */
    private String buildDescription(String payType, Map<String, Object> request) {
        // 如果请求中已经提供了描述，直接使用
        if (request.containsKey("description")) {
            String desc = String.valueOf(request.get("description"));
            if (StringUtils.hasText(desc) && !"null".equals(desc)) {
                return desc;
            }
        }

        // 根据支付类型返回默认描述
        String description;
        switch (payType) {
            case PAY_TYPE_REF:
                description = "裁判服务费";
                break;
            case PAY_TYPE_LIVE:
                description = "直播服务费";
                break;
            case PAY_TYPE_MALL:
                description = "商品购买";
                break;
            default:
                description = "订单支付";
                break;
        }
        return description;
    }

    /**
     * 从通知数据中解析支付类型
     * <p>
     * 通过订单号前缀判断：RF-裁判, LV-直播, YL-商品
     * </p>
     *
     * @param notifyData 通知数据
     * @return 支付类型
     */
    private String parsePayTypeFromNotify(String notifyData) {
        // 简单实现：通过订单号前缀判断
        if (notifyData.contains("RF")) {
            return PAY_TYPE_REF;
        } else if (notifyData.contains("LV")) {
            return PAY_TYPE_LIVE;
        } else if (notifyData.contains("YL")) {
            return PAY_TYPE_MALL;
        }
        // 默认返回裁判支付
        return PAY_TYPE_REF;
    }
}