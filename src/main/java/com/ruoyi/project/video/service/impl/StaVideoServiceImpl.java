package com.ruoyi.project.video.service.impl;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.common.utils.file.MimeTypeUtils;
import com.ruoyi.framework.web.domain.AjaxResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.ruoyi.project.video.mapper.StaVideoMapper;
import com.ruoyi.project.video.domain.StaVideo;
import com.ruoyi.project.video.service.IStaVideoService;
import org.springframework.web.multipart.MultipartFile;

/**
 * 视频录像文件Service业务层处理
 * 
 * @author ruoyi
 * @date 2023-03-28
 */
@Service
public class StaVideoServiceImpl implements IStaVideoService 
{
    private static final Logger log = LoggerFactory.getLogger(StaVideoServiceImpl.class);

    @Autowired
    private StaVideoMapper staVideoMapper;

    //文件上传路径
    @Value("${spring.upload.videopath}")
    private String uploadPath;

    //文件上传服务环境
    @Value("${spring.upload.server}")
    private String serverPath;

    //ffmpeg目录
    @Value("${spring.upload.ffmpeg}")
    private String ffmpegPath;

    /**
     * 查询视频录像文件
     * 
     * @param id 视频录像文件主键
     * @return 视频录像文件
     */
    @Override
    public StaVideo selectStaVideoById(Long id)
    {
        return staVideoMapper.selectStaVideoById(id);
    }

    /**
     * 查询视频录像文件列表
     * 
     * @param staVideo 视频录像文件
     * @return 视频录像文件
     */
    @Override
    public List<StaVideo> selectStaVideoList(StaVideo staVideo)
    {
        List<StaVideo> staVideos = staVideoMapper.selectStaVideoList(staVideo);
        staVideos.forEach(filePath -> filePath.setFilePath(serverPath+filePath.getFilePath()));
        return staVideos;
    }

    /**
     * 新增视频录像文件
     * 
     * @param staVideo 视频录像文件
     * @return 结果
     */
    @Override
    public int insertStaVideo(StaVideo staVideo)
    {
        return staVideoMapper.insertStaVideo(staVideo);
    }

    /**
     * 修改视频录像文件
     * 
     * @param staVideo 视频录像文件
     * @return 结果
     */
    @Override
    public int updateStaVideo(StaVideo staVideo)
    {
//        staVideo.setUpdateTime(DateUtils.getNowDate());
        return staVideoMapper.updateStaVideo(staVideo);
    }

    /**
     * 批量删除视频录像文件
     * 
     * @param ids 需要删除的视频录像文件主键
     * @return 结果
     */
    @Override
    public int deleteStaVideoByIds(Long[] ids)
    {
        return staVideoMapper.deleteStaVideoByIds(ids);
    }

    /**
     * 删除视频录像文件信息
     * 
     * @param id 视频录像文件主键
     * @return 结果
     */
    @Override
    public int deleteStaVideoById(Long id)
    {
        return staVideoMapper.deleteStaVideoById(id);
    }

    /**
     * 上传视频文件
     */
    @Override
    public Object uploadVideo(MultipartFile file, String leagueId, String gameId) throws IOException{
        try
        {
            if (file ==null){
                return "文件不能为空";
            }
            log.info("上传的文件路径在这==============="+uploadPath);
            String path = uploadPath +"league"+leagueId+"/"+"game"+gameId+"/video/";
            String result = FileUploadUtils.upload(path, file, MimeTypeUtils.DEFAULT_ALLOWED_EXTENSION);
//            result = result.replaceFirst ("/home/web/dist","");
            log.info("图片访问路径++" +result);
            StaVideo staVideo = new StaVideo();
            staVideo.setFilePath(result);
            staVideo.setFileName(file.getOriginalFilename());
            staVideoMapper.insertStaVideo(staVideo);
            result = serverPath+result;
            return result;
        }
        catch (Exception e)
        {
            throw new IOException(e.getMessage(), e);
        }
    }

    @Override
    public AjaxResult fullPlayBlack(String leagueId, String gameId) throws Exception {
        // 获取目录路径
        String videoFiles2 = uploadPath + "league" + leagueId + "/game" + gameId + "/";
        File directory = new File(videoFiles2 + "video/");
        System.out.println("比赛所有视频文件目录：" + directory.getAbsolutePath());

        // 递归获取所有MP4文件并预处理
        List<File> videoFiles = getAllMp4Files(directory);
        if (videoFiles == null || videoFiles.isEmpty()) {
            return AjaxResult.error("没有找到视频文件");
        }

        // ---------- 关键修复1：转换所有分片为分段MP4 ----------
        File tempDir = new File(videoFiles2 + "temp/");
        if (!tempDir.exists() && !tempDir.mkdirs()) {
            throw new Exception("无法创建临时目录");
        }

        List<File> processedFiles = new ArrayList<>();
        for (File videoFile : videoFiles) {
            File outputFile = new File(tempDir, videoFile.getName());
            convertToFragmentedMp4(videoFile, outputFile); // 调用转换方法
            processedFiles.add(outputFile);
        }

        // ---------- 关键修复2：生成安全的文件列表 ----------
        String FILE_LIST_PATH = videoFiles2 + "filelist.txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_LIST_PATH))) {
            for (File videoFile : processedFiles) {
                String safePath = videoFile.getAbsolutePath()
                        .replace("\\", "/")
                        .replace("'", "'\\'");
                writer.write("file '" + safePath + "'\n");
            }
        }

        // ---------- 关键修复3：调整FFmpeg命令 ----------
        String outputVideoPath = videoFiles2 + "merger/merged_video.mp4";
        File parentDir = new File(outputVideoPath).getParentFile();
        if (!parentDir.exists() && !parentDir.mkdirs()) {
            throw new Exception("无法创建输出目录");
        }

        String cmd = String.format("%s -y -f concat -safe 0 -fflags +genpts -i \"%s\" -c copy \"%s\"",
                ffmpegPath, FILE_LIST_PATH, outputVideoPath);

        // ---------- 执行命令并捕获错误流 ----------
        try {
            Process process = Runtime.getRuntime().exec(cmd);

            // 打印标准输出和错误流
            Thread stdoutThread = new Thread(() -> {
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.out.println("[FFmpeg] " + line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            stdoutThread.start();

            Thread stderrThread = new Thread(() -> {
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getErrorStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.err.println("[FFmpeg Error] " + line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            stderrThread.start();

            int exitCode = process.waitFor();
            stdoutThread.join();
            stderrThread.join();

            if (exitCode != 0) {
                return AjaxResult.error("视频合并失败，FFmpeg错误码: " + exitCode);
            }

            System.out.println("视频合并完成！");
        } finally {
            // 清理临时文件
            deleteDirectory(tempDir);
            new File(FILE_LIST_PATH).delete();
        }

        return AjaxResult.success(serverPath + outputVideoPath);
    }

    // 新增方法：转换视频为分段MP4格式
    private void convertToFragmentedMp4(File inputFile, File outputFile) throws IOException, InterruptedException {
        String cmd = String.format("%s -y -i \"%s\" -movflags frag_keyframe+empty_moov -c copy \"%s\"",
                ffmpegPath, inputFile.getAbsolutePath(), outputFile.getAbsolutePath());
        Process process = Runtime.getRuntime().exec(cmd);
        process.waitFor();
        if (process.exitValue() != 0) {
            throw new IOException("视频转换失败: " + inputFile.getName());
        }
    }

    // 辅助方法：递归删除目录
    private void deleteDirectory(File dir) {
        if (dir.isDirectory()) {
            for (File file : dir.listFiles()) {
                deleteDirectory(file);
            }
        }
        dir.delete();
    }



    // 递归获取所有MP4文件
    private List<File> getAllMp4Files(File directory) {
        List<File> mp4Files = new ArrayList<>();
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        mp4Files.addAll(getAllMp4Files(file));
                    } else if (file.getName().toLowerCase().endsWith(".mp4")) {
                        mp4Files.add(file);
                    }
                }
            }
        }
        return mp4Files;
    }
}
