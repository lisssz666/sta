package com.ruoyi.mall.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.mall.order.domain.MallOrder;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 订单接口
 */
@Mapper
public interface MallOrderMapper extends BaseMapper<MallOrder> {}