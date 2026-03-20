package com.ruoyi.project.live.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.project.live.domain.LivePerson;

import java.io.IOException;
import java.util.List;

/**
 * 直播人员信息Service接口
 * 描述：定义直播人员信息的业务操作方法
 */
public interface LivePersonService extends IService<LivePerson> {

    /**
     * 新增直播人员
     *
     * @param livePerson 直播人员信息
     * @return 新增是否成功
     */
    boolean saveLivePerson(LivePerson livePerson) throws IOException;

    /**
     * 更新直播人员信息
     *
     * @param livePerson 直播人员信息
     * @return 更新是否成功
     */
    boolean updateLivePersonById(LivePerson livePerson) throws IOException;

    /**
     * 删除直播人员（逻辑删除）
     *
     * @param id 直播人员ID
     * @return 删除是否成功
     */
    boolean removeLivePersonById(Long id);

    /**
     * 根据ID查询直播人员详情
     *
     * @param id 直播人员ID
     * @return 直播人员详情
     */
    LivePerson getLivePersonById(Long id);

    /**
     * 查询所有直播人员列表
     *
     * @return 直播人员列表
     */
    List<LivePerson> listLivePersons();

    /**
     * 查询启用状态的直播人员列表
     *
     * @return 启用状态的直播人员列表
     */
    List<LivePerson> listActiveLivePersons();
}
