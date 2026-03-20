package com.ruoyi.project.live.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.project.live.domain.LivePerson;

import org.apache.ibatis.annotations.Mapper;

/**
 * 直播人员信息Mapper接口
 * 描述：提供对直播人员信息表的基础CRUD操作
 */
@Mapper
public interface LivePersonMapper extends BaseMapper<LivePerson> {
}
