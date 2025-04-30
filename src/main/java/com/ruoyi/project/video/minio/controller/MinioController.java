package com.ruoyi.project.video.minio.controller;

import com.ruoyi.framework.web.domain.AjaxResult;
import com.ruoyi.project.video.minio.config.MinioConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/minio")
public class MinioController {


    @Autowired
    MinioConfig minioConfig;

    @Autowired
    VideoMerger videoMerger;

    @RequestMapping("/list")
    public List<String> list() throws Exception {
        System.out.println("minio list");
        return this.minioConfig.listBucketNames();
    }

    @PostMapping("/upload")
    public AjaxResult upload(@RequestParam("file") MultipartFile multipartFile,
                             @RequestParam("leagueId") String leagueId,
                             @RequestParam("gameId") String gameId) throws Exception {
        return AjaxResult.success(minioConfig.putObject(multipartFile,leagueId,gameId,null));
    }

    // 下载文件
    @GetMapping("/download")
    public void download(@RequestParam("fileName")String fileName,
                         @RequestParam("leagueId")Integer leagueId,
                         @RequestParam("gameId")Integer gameId,
                         HttpServletResponse response) {
        this.minioConfig.download(leagueId,gameId,fileName,response);
    }

    // 创建存储桶
    @PostMapping("/createBucket")
    public boolean createBucket(String bucketName) throws Exception {
        return this.minioConfig.makeBucket(bucketName);
    }

    // 删除存储桶
    @PostMapping("/deleteBucket")
    public boolean deleteBucket(String bucketName) throws Exception {
        return this.minioConfig.removeBucket(bucketName);
    }

    // 列出存储桶中的所有对象名称
    @PostMapping("/listObjectNames")
    public List<String> listObjectNames(String bucketName) throws Exception {
        return this.minioConfig.listObjectNames(bucketName);
    }

    // 删除一个对象
    @PostMapping("/removeObject")
    public boolean removeObject(String bucketName, String objectName) throws Exception {
        return this.minioConfig.removeObject(bucketName, objectName);
    }

    // 文件访问路径
    @PostMapping("/getObjectUrl")
    public String getObjectUrl(String bucketName, String objectName) throws Exception {
        return this.minioConfig.getObjectUrl(bucketName, objectName);
    }

    // 合并比赛视频
    @PostMapping("/fullPlayBlack")
    public AjaxResult fullPlayBlack(@RequestParam("leagueId") String leagueId,@RequestParam("gameId") String gameId) throws Exception {
        return videoMerger.fullPlayBlack(leagueId, gameId);
    }
}