package com.ruoyi.project.team.service.impl;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.project.player.domain.StaPlayer;
import com.ruoyi.project.player.mapper.StaPlayerMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.ruoyi.project.team.mapper.StaTeamMapper;
import com.ruoyi.project.team.domain.StaTeam;
import com.ruoyi.project.team.service.IStaTeamService;
import org.springframework.web.multipart.MultipartFile;

/**
 * 球队信息Service业务层处理
 * 
 * @author ruoyi
 * @date 2023-03-28
 */
@Service
public class StaTeamServiceImpl implements IStaTeamService 
{

    //文件上传路径
    @Value("${spring.upload.path}")
    private String uploadImgPath;

    //服务器
    @Value("${spring.upload.server}")
    private String server;

    @Autowired
    private StaTeamMapper staTeamMapper;

    @Autowired
    private StaPlayerMapper staPlayerMapper;

    /**
     * 查询球队信息
     * 
     * @param id 球队信息主键
     * @return 球队信息
     */
    @Override
    public StaTeam selectStaTeamById(Long id)
    {
        return Optional.ofNullable(staTeamMapper.selectStaTeamById(id))
                .map(staTeam -> {
                    String logoPath = staTeam.getTeamLogoPath();
                    staTeam.setTeamLogoPath(server + logoPath);
                    return staTeam;
                })
                .orElse(null);
    }

    /**
     * 查询球队信息列表
     * 
     * @param staTeam 球队信息
     * @return 球队信息
     */
    @Override
    public List<StaTeam> selectStaTeamList(StaTeam staTeam)
    {
        List<StaTeam> staTeams = staTeamMapper.selectStaTeamList(staTeam);
        // 使用Stream API更新leagueLogoPath
        return staTeams.stream()
                .peek(sta -> {
                    String logoPath = sta.getTeamLogoPath();
                    logoPath = server + logoPath;
                    sta.setTeamLogoPath(logoPath);
                })
                .collect(Collectors.toList());
    }

    /**
     * 新增球队信息
     * 
     * @param staTeam 球队信息
     * @return 结果
     */
    @Override
    public Long insertStaTeam(StaTeam staTeam) throws IOException {
        staTeam.setCreateTime(DateUtils.getNowDate());
        MultipartFile teamLogo = staTeam.getTeamLogo();
        try {
            if (staTeam.getTeamLogo() !=null && !staTeam.getTeamLogo().isEmpty())
            {
                //上传logo
                String teamLogoPath = FileUploadUtils.upload(uploadImgPath, teamLogo);
                staTeam.setTeamLogoPath(teamLogoPath);
            }
            staTeamMapper.insertStaTeam(staTeam);
            //默认添加一个“其他”球员
            StaPlayer staPlayer = new StaPlayer();
            staPlayer.setTeamId(staTeam.getId());
            staPlayer.setJerseyNumber(100);
            staPlayer.setName("其他");
            staPlayerMapper.insertStaPlayer(staPlayer);
            return staTeam.getId();
        }
        catch (Exception e)
        {
            throw new IOException(e.getMessage(), e);
        }
    }

    /**
     * 修改球队信息
     * 
     * @param staTeam 球队信息
     * @return 结果
     */
    @Override
    public int updateStaTeam(StaTeam staTeam) throws IOException {
        staTeam.setUpdateTime(DateUtils.getNowDate());
        if (staTeam.getIdStr() != null  && !staTeam.getIdStr().isEmpty()){
            String idStr = staTeam.getIdStr();
            Long i = Long.valueOf(idStr);
            staTeam.setId(i);
        }
        MultipartFile teamLogo = staTeam.getTeamLogo();
        if (teamLogo != null){
            //上传logo
            String logoPath = FileUploadUtils.upload(uploadImgPath, teamLogo);
            staTeam.setTeamLogoPath(logoPath);
        }
        return staTeamMapper.updateStaTeam(staTeam);
    }

    /**
     * 批量删除球队信息
     * 
     * @param ids 需要删除的球队信息主键
     * @return 结果
     */
    @Override
    public int deleteStaTeamByIds(Long[] ids)
    {
        return staTeamMapper.deleteStaTeamByIds(ids);
    }

    /**
     * 删除球队信息信息
     * 
     * @param id 球队信息主键
     * @return 结果
     */
    @Override
    public int deleteStaTeamById(Long id)
    {
        return staTeamMapper.deleteStaTeamById(id);
    }
}
