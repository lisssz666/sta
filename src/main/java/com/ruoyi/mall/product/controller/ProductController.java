package com.ruoyi.mall.product.controller;
import com.ruoyi.framework.web.controller.BaseController;
import com.ruoyi.framework.web.domain.AjaxResult;
import com.ruoyi.mall.product.domain.MallProduct;
import com.ruoyi.mall.product.service.MallProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * 球队分组控制器
 */
@RestController
@RequestMapping("/product")
public class ProductController extends BaseController {

    @Autowired
    private MallProductService productService;

    /** 新增商品 */
    @PostMapping("/add")
    public AjaxResult addProduct(MallProduct dto) throws IOException {
        productService.saveProduct(dto);
        return AjaxResult.success(dto.getId());
    }

    /** 删除商品 */
    @DeleteMapping("/delete")
    public AjaxResult delProduct(Long id) {
        return AjaxResult.success(productService.removeProductById(id));
    }

    /** 修改商品 */
    @PutMapping("/update")
    public AjaxResult updProduct(MallProduct dto) throws IOException{
        return AjaxResult.success(productService.updateProductById(dto));
    }

    /** 商品列表（可按商位筛选） */
    @GetMapping("/list")
    public AjaxResult listProduct(@RequestParam(required = false) Long merchantId) {
        return AjaxResult.success(productService.listProductsByMerchantId(merchantId));
//        return productService.lambdaQuery()
//                .eq(merchantId != null, MallProduct::getMerchantId, merchantId)
//                .list();
    }

    /** 商品详情 */
    @GetMapping("/getProductById")
    public AjaxResult getProduct(Long id) {
        return AjaxResult.success(productService.getProductById(id));
    }
}

