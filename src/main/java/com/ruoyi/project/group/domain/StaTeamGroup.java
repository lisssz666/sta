package com.ruoyi.project.group.domain;

import com.ruoyi.framework.web.domain.BaseEntity;
import com.ruoyi.project.team.domain.StaTeam;
import lombok.Data;

import java.util.List;

/**
 * 球队分组实体类
 */
@Data
public class StaTeamGroup extends BaseEntity{

    /** 主键ID */
    private Long id;

    /** 联赛ID */
    private Long leagueId;

    /** 分组名称 */
    private String groupName;

    private String teamIds; // 用String存储JSON

    private List<StaTeam> teamInfo;

   /* // 将 teamIds 字段转换为 List<Long>
    public List<Long> getTeamIdsAsList() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(teamIds, new TypeReference<List<Long>>() {});
    }

    // 将 List<Long> 转换为 JSON 字符串
    public void setTeamIdsFromList(List<Long> teamIdsList) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        this.teamIds = mapper.writeValueAsString(teamIdsList);
    }*/

}


