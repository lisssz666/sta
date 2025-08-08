package com.ruoyi.mall.product.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.ruoyi.framework.web.domain.BaseEntity;
import com.ruoyi.project.team.domain.StaTeam;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

// 商品
@Data
@TableName("mall_product")
public class MallProduct {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long merchantId;
    private String name;
    private String coverImg;
    @TableField(exist = false)
    private MultipartFile coverImgFile  ;
    /** 原价 */
    private String price;
    /** 折扣价 */
    private String discountPrice;
    private Integer monthSales;
    private Integer status;
    /** 商品标语 */
    private String slogan;
    /** 销量标签 */
    private String salesTag;
    /** 库存（字符串） */
    private String stock;
    /** 折扣系数（字符串） */
    private String discount;
    /** 逻辑删除 0未删 1已删 */
    @TableLogic(value = "0", delval = "1")
    private Integer deleted;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}