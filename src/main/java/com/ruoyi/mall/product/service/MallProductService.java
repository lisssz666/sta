package com.ruoyi.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.mall.product.domain.MallProduct;

import java.io.IOException;
import java.util.List;

/**
 * 球队分组服务接口
 */
public interface MallProductService extends IService<MallProduct> {
    /** 新增商品 */
    boolean saveProduct(MallProduct product) throws IOException;
    /** 根据ID删除商品 */
    boolean removeProductById(Long id);
    /** 根据ID修改商品 */
    boolean updateProductById(MallProduct product)  throws IOException;
    /** 根据ID查询商品 */
    MallProduct getProductById(Long id);
    /** 根据商位ID查询商品列表 */
    List<MallProduct> listProductsByMerchantId(Long merchantId);
}