package com.ruoyi.mall.order.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.mall.order.domain.MallOrderItem;
import com.ruoyi.mall.order.mapper.MallOrderItemMapper;
import com.ruoyi.mall.order.service.MallOrderItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class MallOrderItemServiceImpl
        extends ServiceImpl<MallOrderItemMapper, MallOrderItem>
        implements MallOrderItemService {

    @Autowired
    private MallOrderItemMapper mallOrderItemMapper;

    @Override
    public List<MallOrderItem> listItemsByOrderId(Long id) {
        return mallOrderItemMapper.listItemsByOrderId(id);
    }
}
