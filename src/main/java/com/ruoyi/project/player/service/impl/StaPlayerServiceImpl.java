package com.ruoyi.project.player.service.impl;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.project.statistic.domain.StaPlayerStatistic;
import com.ruoyi.project.statistic.service.impl.StaPlayerStatisticServiceImpl;
import com.ruoyi.project.team.domain.StaTeam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.ruoyi.project.player.mapper.StaPlayerMapper;
import com.ruoyi.project.player.domain.StaPlayer;
import com.ruoyi.project.player.service.IStaPlayerService;
import org.springframework.web.multipart.MultipartFile;

/**
 * 运动员信息Service业务层处理
 * 
 * @author ruoyi
 * @date 2023-03-28
 */
@Service
public class StaPlayerServiceImpl implements IStaPlayerService 
{

    //文件上传路径
    @Value("${spring.upload.path}")
    private String uploadImgPath;

    //文件上传路径
    @Value("${spring.upload.server}")
    private String server;

    @Autowired
    private StaPlayerMapper staPlayerMapper;

    /**
     * 查询运动员信息
     * 
     * @param id 运动员信息主键
     * @return 运动员信息
     */
    @Override
    public StaPlayer selectStaPlayerById(Long id)
    {
        return Optional.ofNullable(staPlayerMapper.selectStaPlayerById(id))
                .map(player -> {
                    String logoPath = player.getLogoPath();
                    player.setLogoPath(server + "/" + logoPath);
                    return player;
                })
                .orElse(null);
    }

    /**
     * 查询运动员信息列表
     * 
     * @param staPlayer 运动员信息
     * @return 运动员信息
     */
    @Override
    public List<StaPlayer> selectStaPlayerList(StaPlayer staPlayer)
    {
        List<StaPlayer> staPlayers = staPlayerMapper.selectStaPlayerList(staPlayer);
        // 使用Stream API更新leagueLogoPath
        return staPlayers.stream()
                .peek(sta -> {
                    String logoPath = sta.getLogoPath();
                    logoPath = server + "/" + logoPath;
                    sta.setLogoPath(logoPath);
                })
                .collect(Collectors.toList());
    }

    /**
     * 新增运动员信息
     * 
     * @param staPlayer 运动员信息
     * @return 结果
     */
    @Override
    public Long insertStaPlayer(StaPlayer staPlayer) throws IOException {
        staPlayer.setCreateTime(DateUtils.getNowDate());
        MultipartFile logo = staPlayer.getLogo();
        System.out.print("球员logo文件 ： "+logo);
        if (logo != null)
        {
            //上传球员logo
            String path = FileUploadUtils.upload(uploadImgPath, logo);
            staPlayer.setLogoPath(path);
        }
        staPlayerMapper.insertStaPlayer(staPlayer);
        return staPlayer.getId();
    }

    /**
     * 修改运动员信息
     * 
     * @param staPlayer 运动员信息
     * @return 结果
     */
    @Override
    public int updateStaPlayer(StaPlayer staPlayer)
    {
        if (staPlayer.getIdStr() != null  && !staPlayer.getIdStr().isEmpty()){
            String idStr = staPlayer.getIdStr();
            Long i = Long.valueOf(idStr);
            staPlayer.setId(i);
        }
        staPlayer.setUpdateTime(DateUtils.getNowDate());
        return staPlayerMapper.updateStaPlayer(staPlayer);
    }

    /**
     * 批量删除运动员信息
     * 
     * @param ids 需要删除的运动员信息主键
     * @return 结果
     */
    @Override
    public int deleteStaPlayerByIds(Long[] ids)
    {
        return staPlayerMapper.deleteStaPlayerByIds(ids);
    }

    /**
     * 删除运动员信息信息
     * 
     * @param id 运动员信息主键
     * @return 结果
     */
    @Override
    public int deleteStaPlayerById(Long id)
    {
        return staPlayerMapper.deleteStaPlayerById(id);
    }

}
