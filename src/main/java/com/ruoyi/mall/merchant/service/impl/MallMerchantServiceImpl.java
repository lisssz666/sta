package com.ruoyi.mall.merchant.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.mall.merchant.domain.MallMerchant;
import com.ruoyi.mall.merchant.mapper.MallMerchantMapper;
import com.ruoyi.mall.merchant.service.MallMerchantService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 商位实现
 */
@Service
public class MallMerchantServiceImpl extends ServiceImpl<MallMerchantMapper, MallMerchant>
        implements MallMerchantService {

    //文件上传路径
    @Value("${spring.upload.path}")
    private String uploadImgPath;

    //文件上传路径
    @Value("${spring.upload.server}")
    private String server;

    @Override
    public boolean saveMerchant(MallMerchant merchant) throws IOException{
        MultipartFile avatar = merchant.getCoverImgFile();
        MultipartFile logoFile = merchant.getMerchantLogoFile();
        try {
            if (avatar != null && !avatar.isEmpty())
            {
                //上传商铺背景图
                String avatarPath = FileUploadUtils.upload(uploadImgPath, avatar);
                merchant.setCoverImg(avatarPath);
            }
            if (logoFile != null && !logoFile.isEmpty())
            {
                //上传商铺logo
                String logoPath = FileUploadUtils.upload(uploadImgPath, logoFile);
                merchant.setMerchantLogo(logoPath);
            }
            return save(merchant);
        }
        catch (Exception e)
        {
            throw new IOException(e.getMessage(), e);
        }
    }

    @Override
    public boolean removeMerchantById(Long id) {
        return removeById(id);
    }

    @Override
    public boolean updateMerchantById(MallMerchant merchant) throws IOException {
        MultipartFile avatar = merchant.getCoverImgFile();
        MultipartFile logoFile = merchant.getMerchantLogoFile();
        try {
            if (avatar != null && !avatar.isEmpty())
            {
                //上传商铺背景图
                String avatarPath = FileUploadUtils.upload(uploadImgPath, avatar);
                merchant.setCoverImg(avatarPath);
            }
            if (logoFile != null && !logoFile.isEmpty())
            {
                //上传商铺logo
                String logoPath = FileUploadUtils.upload(uploadImgPath, logoFile);
                merchant.setMerchantLogo(logoPath);
            }
            return updateById(merchant);
        }
        catch (Exception e)
        {
            throw new IOException(e.getMessage(), e);
        }

    }

    @Override
    public MallMerchant getMerchantById(Long id) {
        return Optional.ofNullable(getById(id))
                .map(p -> {
                    p.setCoverImg(server + p.getCoverImg());
                    p.setMerchantLogo(server + p.getMerchantLogo());
                    return p;
                })
                .orElse(null);
    }

    @Override
    public List<MallMerchant> listMerchants() {
        return Optional.ofNullable(list())
                .map(list -> list.stream()
                        .peek(m -> m.setCoverImg(server + m.getCoverImg()))
                        .collect(Collectors.toList()))
                .orElse(Collections.emptyList());
    }
}

