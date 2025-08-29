package com.ruoyi.mall.merchant.controller;
import com.ruoyi.framework.web.controller.BaseController;
import com.ruoyi.framework.web.domain.AjaxResult;
import com.ruoyi.mall.merchant.domain.MallMerchant;
import com.ruoyi.mall.merchant.service.MallMerchantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * 球队分组控制器
 */
@RestController
@RequestMapping("/merchant")
public class MerchantController  extends BaseController {

    @Autowired
    private  MallMerchantService merchantService;

    /**
     * 新增商位
     */
    @PostMapping("/add")
    public AjaxResult addMerchant(MallMerchant dto) throws IOException {
        merchantService.saveMerchant(dto);
        return AjaxResult.success(dto.getId());
    }

    /**
     * 删除商位
     */
    @DeleteMapping("/delete")
    public AjaxResult delMerchant(Long id) {
        return AjaxResult.success(merchantService.removeMerchantById(id));
    }

    /**
     * 修改商位
     */
    @PutMapping("/update")
    public AjaxResult updMerchant(MallMerchant dto) throws IOException{
        return AjaxResult.success(merchantService.updateMerchantById(dto));
    }

    /**
     * 商位列表
     */
    @GetMapping("/list")
    public AjaxResult listMerchant() {
        return AjaxResult.success(merchantService.listMerchants());
    }

    /**
     * 商位详情
     */
    @GetMapping("/getMerchantById")
    public AjaxResult getMerchant(Long id) {
        return AjaxResult.success(merchantService.getMerchantById(id));
    }
}
