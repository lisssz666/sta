package com.ruoyi.project.match.service.impl;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.file.FileUploadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.ruoyi.project.match.mapper.StaLeagueMatchMapper;
import com.ruoyi.project.match.domain.StaLeagueMatch;
import com.ruoyi.project.match.service.IStaLeagueMatchService;
import org.springframework.util.CollectionUtils;

/**
 * 联赛信息Service业务层处理
 * 
 * @author ruoyi
 * @date 2023-04-20
 */
@Service
public class StaLeagueMatchServiceImpl extends ServiceImpl<StaLeagueMatchMapper,StaLeagueMatch> implements IStaLeagueMatchService
{
    //文件上传路径
    @Value("${spring.upload.path}")
    private String uploadImgPath;

    //文件上传路径
    @Value("${spring.upload.server}")
    private String server;

    @Autowired
    private StaLeagueMatchMapper staLeagueMatchMapper;

    /**
     * 查询联赛信息
     * 
     * @param id 联赛信息主键
     * @return 联赛信息
     */
    @Override
    public StaLeagueMatch selectStaLeagueMatchById(Long id)
    {
        return staLeagueMatchMapper.selectStaLeagueMatchById(id);
    }

    /**
     * 查询联赛信息列表
     * 
     * @param staLeagueMatch 联赛信息
     * @return 联赛信息
     */
    @Override
    public List<StaLeagueMatch> selectStaLeagueMatchList(StaLeagueMatch staLeagueMatch)
    {

        // 获取初始列表
        List<StaLeagueMatch> staLeagueMatches = staLeagueMatchMapper.selectStaLeagueMatchList(staLeagueMatch);

        // 使用Stream API更新leagueLogoPath
        return staLeagueMatches.stream()
                .peek(sta -> {
                    String leagueLogoPath = sta.getLeagueLogoPath();
                    leagueLogoPath = server + leagueLogoPath;
                    sta.setLeagueLogoPath(leagueLogoPath);
                    String backgroundPath = sta.getLeagueBgImagePath();
                    backgroundPath = server + backgroundPath;
                    sta.setLeagueBgImagePath(backgroundPath);
                })
                .collect(Collectors.toList());
    }

    /**
     * 新增联赛信息
     * 
     * @param staLeagueMatch 联赛信息
     * @return 结果
     */
    @Override
    public int insertStaLeagueMatch(StaLeagueMatch staLeagueMatch) throws IOException {
        staLeagueMatch.setCreateTime(DateUtils.getNowDate());
        System.out.print("leagueLogoFile文件: "+ staLeagueMatch.getLeagueLogo());
        System.out.print("backgroundImageFile文件: "+ staLeagueMatch.getLeagueBgImage());
        System.out.print("staLeagueMatch 对象: "+ staLeagueMatch);
//        MultipartFile leagueLogoFile = staLeagueMatch.getLeagueLogo();
//        MultipartFile backgroundImageFile = staLeagueMatch.getLeagueBgImage();
        try {
            if (staLeagueMatch.getLeagueLogo() != null && !staLeagueMatch.getLeagueLogo().isEmpty())
            {
                //上传logo
                String logoPath = FileUploadUtils.upload(uploadImgPath, staLeagueMatch.getLeagueLogo());
                staLeagueMatch.setLeagueLogoPath(logoPath);
            }
            if (staLeagueMatch.getLeagueBgImage() != null && !staLeagueMatch.getLeagueBgImage().isEmpty())
            {
                //上传赛事背景图
                String groundPath = FileUploadUtils.upload(uploadImgPath, staLeagueMatch.getLeagueBgImage());
                staLeagueMatch.setLeagueBgImagePath(groundPath);
            }
            staLeagueMatchMapper.insertStaLeagueMatch(staLeagueMatch);
            return staLeagueMatch.getId();
        }
        catch (Exception e)
        {
            throw new IOException(e.getMessage(), e);
        }
    }

    /**
     * 修改联赛信息
     * 
     * @param staLeagueMatch 联赛信息
     * @return 结果
     */
    @Override
    public int updateStaLeagueMatch(StaLeagueMatch staLeagueMatch) throws IOException
    {
//        staLeagueMatch.setUpdateTime(DateUtils.getNowDate());
        //传字符串id
        if (staLeagueMatch.getIdStr() != null  && !staLeagueMatch.getIdStr().isEmpty()){
            String idStr = staLeagueMatch.getIdStr();
            Integer i = Integer.valueOf(idStr);
            staLeagueMatch.setId(i);
        }
        try {
            if (staLeagueMatch.getLeagueLogo() != null && !staLeagueMatch.getLeagueLogo().isEmpty())
            {
                //上传logo
                String logoPath = FileUploadUtils.upload(uploadImgPath, staLeagueMatch.getLeagueLogo());
                staLeagueMatch.setLeagueLogoPath(logoPath);
            }
            if (staLeagueMatch.getLeagueBgImage() != null && !staLeagueMatch.getLeagueBgImage().isEmpty())
            {
                //上传赛事背景图
                String groundPath = FileUploadUtils.upload(uploadImgPath, staLeagueMatch.getLeagueBgImage());
                staLeagueMatch.setLeagueBgImagePath(groundPath);
            }
            staLeagueMatchMapper.updateStaLeagueMatch(staLeagueMatch);
            return staLeagueMatch.getId();
        }
        catch (Exception e)
        {
            throw new IOException(e.getMessage(), e);
        }
    }

    /**
     * 批量删除联赛信息
     * 
     * @param ids 需要删除的联赛信息主键
     * @return 结果
     */
    @Override
    public int deleteStaLeagueMatchByIds(Long[] ids)
    {
        return staLeagueMatchMapper.deleteStaLeagueMatchByIds(ids);
    }

    /**
     * 删除联赛信息信息
     * 
     * @param id 联赛信息主键
     * @return 结果
     */
    @Override
    public int deleteStaLeagueMatchById(Long id)
    {
        return staLeagueMatchMapper.deleteStaLeagueMatchById(id);
    }


    @Override
    public List<StaLeagueMatch> getStaLeagueMatch(Long id){
        LambdaQueryWrapper<StaLeagueMatch> leagueMatchLambdaQueryMrapper = new LambdaQueryWrapper<StaLeagueMatch>()
                .eq(StaLeagueMatch::getId,id);
        leagueMatchLambdaQueryMrapper.eq(StaLeagueMatch::getCoOrganizer,"qq");
        leagueMatchLambdaQueryMrapper.orderByDesc(StaLeagueMatch::getGameEndtime);

        StaLeagueMatch byId = this.getById(314);
        StaLeagueMatch staLeagueMatch = staLeagueMatchMapper.selectById(314);
        List<StaLeagueMatch> staLeagueMatches = staLeagueMatchMapper.selectList(leagueMatchLambdaQueryMrapper);
        if (CollectionUtils.isEmpty(staLeagueMatches)){
            return null;
        }
        return staLeagueMatches;
    }

    @Override
    public Integer updateAccessCount() {
        Integer res = staLeagueMatchMapper.updateAccessCount();
        return null;
    }

    @Override
    public Integer getAccessCount() {
        return staLeagueMatchMapper.getAccessCount();
    }
}
