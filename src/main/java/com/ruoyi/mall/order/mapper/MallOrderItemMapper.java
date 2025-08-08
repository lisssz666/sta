package com.ruoyi.mall.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.mall.order.domain.MallOrderItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MallOrderItemMapper extends BaseMapper<MallOrderItem> {
    @Select("SELECT * FROM mall_order_item WHERE order_id = #{orderId} AND deleted = 0")
    List<MallOrderItem> listItemsByOrderId(Long orderId);
}