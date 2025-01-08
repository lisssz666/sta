package com.ruoyi.project.match.service;

import java.io.IOException;
import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.project.match.domain.StaLeagueMatch;
import org.springframework.web.multipart.MultipartFile;

/**
 * 联赛信息Service接口
 * 
 * @author ruoyi
 * @date 2023-04-20
 */
public interface IStaLeagueMatchService  extends IService<StaLeagueMatch>
{
    /**
     * 查询联赛信息
     * 
     * @param id 联赛信息主键
     * @return 联赛信息
     */
    public StaLeagueMatch selectStaLeagueMatchById(Long id);

    /**
     * 查询联赛信息列表
     * 
     * @param staLeagueMatch 联赛信息
     * @return 联赛信息集合
     */
    public List<StaLeagueMatch> selectStaLeagueMatchList(StaLeagueMatch staLeagueMatch);

    /**
     * 新增联赛信息
     * 
     * @param staLeagueMatch 联赛信息
     * @return 结果
     */
    public int insertStaLeagueMatch(StaLeagueMatch staLeagueMatch) throws IOException;

    /**
     * 修改联赛信息
     * 
     * @param staLeagueMatch 联赛信息
     * @return 结果
     */
    public int updateStaLeagueMatch(StaLeagueMatch staLeagueMatch) throws IOException;

    /**
     * 批量删除联赛信息
     * 
     * @param ids 需要删除的联赛信息主键集合
     * @return 结果
     */
    public int deleteStaLeagueMatchByIds(Long[] ids);

    /**
     * 删除联赛信息信息
     * 
     * @param id 联赛信息主键
     * @return 结果
     */
    public int deleteStaLeagueMatchById(Long id);

    public List<StaLeagueMatch> getStaLeagueMatch(Long id);

    Integer updateAccessCount();

    Integer getAccessCount();
}
