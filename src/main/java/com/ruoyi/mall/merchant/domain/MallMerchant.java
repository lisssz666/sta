package com.ruoyi.mall.merchant.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商位
 */

@Data
@TableName("mall_merchant")
public class MallMerchant {

    /** 主键ID，自增 */
    @TableId(type = IdType.AUTO)
    private Long id;
    /** 商户名称 */
    private String name;
    /** 商户地址 */
    private String address;
    /** 地址简称 */
    private String addressAbbr;
    /** 联系电话 */
    private String phone;
    /** 营业时间 */
    private String openTime;
    /** 0停业 1营业 */
    private Integer status;
    /** 是否隐藏 1是 0否 */
    private Integer isHide;
    /** 封面图URL */
    private String coverImg;
    /** 临时上传文件，不入库 */
    @TableField(exist = false)
    private MultipartFile coverImgFile;
    /** 评分 */
    private String score;
    /** 评论数 */
    private Integer commentNum;
    /** 经营类别 */
    private String category;
    /** 距离(km) */
    private String distanceKm;
    /** 0否1是：停车便利 */
    private String parking;
    /** 0否1是：24小时营业 */
    private String openDay;
    /** 榜单描述 */
    private String rankText;
    /** 配送方式：到店/外卖/其他 */
    private String deliveryType;
    /** 商位logo（URL或路径） */
    private String merchantLogo;
    /** 商位logo临时上传文件，不入库 */
    @TableField(exist = false)
    private MultipartFile merchantLogoFile;
    /** 逻辑删除 0未删1已删 */
    @TableLogic(value = "0", delval = "1")
    private Integer deleted;
    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}


