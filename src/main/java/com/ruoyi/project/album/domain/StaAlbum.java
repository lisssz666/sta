package com.ruoyi.project.album.domain;

import com.ruoyi.framework.web.domain.BaseEntity;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * 球队信息对象 sta_team
 * 
 * @author ruoyi
 * @date 2023-03-28
 */
@Data
public class StaAlbum extends BaseEntity{
    /** 主键ID */
    private Long id;

    /** 联赛ID */
    private Long leagueId;

    /** 相册名称 */
    private String albumName;

    /** 相册权限 */
    private String albumPermission;

    /** 所属相册ID */
    private Long parentAlbumId;

    /** 照片路径 */
    private String photoPath;

    /** 照片文件 */
    private MultipartFile photoFile;
}


