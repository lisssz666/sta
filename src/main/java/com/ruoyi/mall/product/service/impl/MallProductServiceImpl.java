package com.ruoyi.mall.product.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.mall.product.domain.MallProduct;
import com.ruoyi.mall.product.mapper.MallProductMapper;
import com.ruoyi.mall.product.service.MallProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


// 商品实现
@Service
public class MallProductServiceImpl extends ServiceImpl<MallProductMapper, MallProduct>
        implements MallProductService {

    //文件上传路径
    @Value("${spring.upload.path}")
    private String uploadImgPath;

    //文件上传路径
    @Value("${spring.upload.server}")
    private String server;

    @Override
    public boolean saveProduct(MallProduct product) throws IOException{
        MultipartFile avatar = product.getCoverImgFile();
        try {
            if (avatar != null && !avatar.isEmpty())
            {
                //上传
                String avatarPath = FileUploadUtils.upload(uploadImgPath, avatar);
                product.setCoverImg(avatarPath);
            }
            return save(product);
        }
        catch (Exception e)
        {
            throw new IOException(e.getMessage(), e);
        }
    }

    @Override
    public boolean removeProductById(Long id) {
        return removeById(id);
    }

    @Override
    public boolean updateProductById(MallProduct product) throws IOException{
        MultipartFile avatar = product.getCoverImgFile();
        try {
            if (avatar != null && !avatar.isEmpty())
            {
                //上传
                String avatarPath = FileUploadUtils.upload(uploadImgPath, avatar);
                product.setCoverImg(avatarPath);
            }
            return updateById(product);
        }
        catch (Exception e)
        {
            throw new IOException(e.getMessage(), e);
        }
    }

    @Override
    public MallProduct getProductById(Long id) {
        return Optional.ofNullable(getById(id))
                .map(p -> {
                    p.setCoverImg(server + p.getCoverImg());
                    return p;
                })
                .orElse(null);
    }

    @Override
    public List<MallProduct> listProductsByMerchantId(Long merchantId) {
        return Optional.ofNullable(
                        lambdaQuery()
                                .eq(MallProduct::getMerchantId, merchantId)
                                .list()
                )
                .map(list -> list.stream()
                        .peek(p -> p.setCoverImg(server + p.getCoverImg()))
                        .collect(Collectors.toList()))
                .orElse(Collections.emptyList());
    }
}