package com.ruoyi.project.video.service.impl;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

import javax.annotation.PostConstruct;

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

    private static String FFMPEG_PATH;   //安装的ffmpeg目录

    @PostConstruct
    public void init() {
        FFMPEG_PATH = ffmpegPath;
    }

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
    public AjaxResult fullPlayBlack(String leagueId, String gameId) {
        Path baseDir = Paths.get(uploadPath, "league" + leagueId, "game" + gameId);
        Path videoDir = baseDir.resolve("video");
        Path outputDir = baseDir.resolve("merger");
        Path outputVideo = outputDir.resolve("merged_video.mp4");
        Path fileListPath = baseDir.resolve("filelist.txt");

        // 1. 验证视频目录有效性
        if (!Files.isDirectory(videoDir)) {
            return AjaxResult.error("视频目录不存在: " + videoDir);
        }

        // 2. 获取有序视频文件列表
        List<Path> videoFiles;
        try {
            videoFiles = getAllMp4Files(videoDir);
            if (videoFiles.isEmpty()) {
                return AjaxResult.error("未找到MP4视频文件");
            }
        } catch (IOException e) {
            return AjaxResult.error("视频文件扫描失败: " + e.getMessage());
        }

        // 3. 创建输出目录
        try {
            Files.createDirectories(outputDir);
        } catch (IOException e) {
            return AjaxResult.error("创建输出目录失败: " + e.getMessage());
        }

        // 4. 生成FFmpeg文件列表
        try {
            generateFileList(fileListPath, videoFiles);
        } catch (IOException e) {
            return AjaxResult.error("文件列表生成失败: " + e.getMessage());
        }

        // 5. 执行FFmpeg命令
        try {
            executeFfmpegCommand(fileListPath, outputVideo);
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            return AjaxResult.error("视频合并失败: " + e.getMessage());
        } finally {
            cleanupTempFile(fileListPath);
        }

        return AjaxResult.success(serverPath + outputVideo.toString().replace(File.separator, "/"));
    }

    private List<Path> getAllMp4Files(Path directory) throws IOException {
        try (Stream<Path> paths = Files.list(directory)) {
            return paths
                    .filter(path -> path.toString().toLowerCase().endsWith(".mp4"))
                    .sorted(Comparator.comparing(Path::getFileName))
                    .collect(Collectors.toList());
        }
    }

    private void generateFileList(Path fileListPath, List<Path> videoFiles) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(fileListPath)) {
            for (Path videoFile : videoFiles) {
                String escapedPath = videoFile.toAbsolutePath()
                        .toString()
                        .replace("'", "'\\''");
                writer.write(String.format("file '%s'%n", escapedPath));
            }
        }
    }

    private void executeFfmpegCommand(Path fileListPath, Path outputVideo)
            throws IOException, InterruptedException {

        List<String> command = new ArrayList<>();
        command.add(FFMPEG_PATH);
        Collections.addAll(command,
                "-f", "concat",
                "-safe", "0",
                "-i", fileListPath.toString(),
                "-c", "copy",
                "-y",
                "-loglevel", "warning",  // 精简日志输出
                outputVideo.toString()
        );

        ProcessBuilder pb = new ProcessBuilder(command)
                .redirectErrorStream(true);  // 合并标准错误流

        Process process = pb.start();
        consumeOutputStream(process.getInputStream());  // 防止缓冲区阻塞

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new IOException("FFmpeg异常退出，代码: " + exitCode);
        }
    }

    private void consumeOutputStream(InputStream inputStream) {
        new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(inputStream))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println("[FFmpeg] " + line);
                }
            } catch (IOException e) {
                System.err.println("日志输出流处理异常: " + e.getMessage());
            }
        }).start();
    }

    private void cleanupTempFile(Path fileListPath) {
        try {
            Files.deleteIfExists(fileListPath);
        } catch (IOException e) {
            System.err.println("临时文件删除失败: " + fileListPath);
        }
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
