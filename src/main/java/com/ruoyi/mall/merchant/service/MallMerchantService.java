package com.ruoyi.mall.merchant.service;

import com.ruoyi.mall.merchant.domain.MallMerchant;

import java.io.IOException;
import java.util.List;

/**
 * 商位
 */
public interface MallMerchantService {
    /** 新增商位 */
    boolean saveMerchant(MallMerchant merchant) throws IOException;
    /** 根据ID删除商位 */
    boolean removeMerchantById(Long id);
    /** 根据ID修改商位 */
    boolean updateMerchantById(MallMerchant merchant);
    /** 根据ID查询商位 */
    MallMerchant getMerchantById(Long id);
    /** 查询所有商位 */
    List<MallMerchant> listMerchants();
}

