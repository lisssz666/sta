package com.ruoyi.project.video.clip;

import com.ruoyi.common.exception.file.InvalidExtensionException;
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.common.utils.file.MimeTypeUtils;
import com.ruoyi.framework.web.domain.server.Sys;
import com.ruoyi.project.video.domain.StaVideo;
import com.ruoyi.project.video.mapper.StaVideoMapper;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class VideoProcessor {

    private static final Logger log = LoggerFactory.getLogger(VideoProcessor.class);

    @Autowired
    private StaVideoMapper staVideoMapper;

    //文件上传路径
    @Value("${spring.upload.videopath}")
    private String uploadPath;

    //文件服务
    @Value("${spring.upload.server}")
    private String server;

    //ffmpeg
    @Value("${spring.upload.ffmpeg}")
    private String FFMPEG_PATH;

    // 合并完视频-生成个人集锦视频
    public String createHighlightVideo(String videoPath, List<Integer> goalTimes, String leagueId, String gameId,Integer playerId,String nameNum,Long teamId) {
        try {
            System.out.println("goalTimes :" +goalTimes);
            System.out.println("videoPath :" +videoPath);
            System.out.println("playerId :" +playerId);
            // 1. 初始化路径和目录
//            String videoPath = saveVideoFile(file, leagueId, gameId);
            String baseDir = Paths.get(uploadPath, "league" + leagueId, "game" + gameId).toString();
            // 创建必要的目录
            createDirectories(baseDir, "output", "highlights","highlights/"+playerId);
            System.out.println("生成视频片段 :");
            // 2. 生成视频片段
            List<String> clipPaths = generateVideoClips(videoPath, goalTimes, baseDir);

            System.out.println("合并视频片段 :");
            // 3. 合并视频片段
            String highlightPath = mergeVideoClips(clipPaths, baseDir, playerId);

            //将每个人的个人集锦视频地址存到数据库
            StaVideo staVideo = new StaVideo();
            staVideo.setType(3);
            staVideo.setLeagueId(leagueId);  //联赛id
            staVideo.setGameId(gameId);  //比赛id
            String teamIdStr = Optional.ofNullable(teamId).map(Object::toString).orElse(null);
            staVideo.setTeamId(teamIdStr); //球队id
            staVideo.setFilePath(highlightPath);
            staVideo.setFileName(nameNum);
            staVideoMapper.insertStaVideo(staVideo);

//            return server + Paths.get(baseDir, "highlights",playerId.toString(), "highlight.mp4").toString();
            return server + highlightPath;
        } catch (IOException | InterruptedException e) {
            log.error("生成集锦视频失败", e);
            throw new RuntimeException("生成集锦视频失败: " + e.getMessage());
        }
    }

    // 通过上传文件和进球时间点-生成个人集锦视频
    public String createHighlightUploadVideo(MultipartFile file, List<Integer> goalTimes,Integer playerId) {
        try {
            System.out.println("goalTimes :" +goalTimes);
            System.out.println("videoPath :" +file);
            System.out.println("playerId :" +playerId);
            // 1. 初始化路径和目录
            String videoPath = saveVideoFile(file);
            String baseDir = Paths.get(uploadPath, "upload").toString();

            // 创建必要的目录
            createDirectories(baseDir, "output", "highlights","highlights/"+playerId);

            System.out.println("生成视频片段 :");
            // 2. 生成视频片段
            List<String> clipPaths = generateVideoClips(videoPath, goalTimes, baseDir);

            System.out.println("合并视频片段 :");
            // 3. 合并视频片段
            String highlightPath = mergeVideoClips(clipPaths, baseDir, playerId);

            return server + Paths.get(baseDir, "highlights",playerId.toString(), "highlight.mp4").toString();
        } catch (IOException | InterruptedException | InvalidExtensionException e) {
            log.error("生成集锦视频失败", e);
            throw new RuntimeException("生成集锦视频失败: " + e.getMessage());
        }
    }


    /**
     * 保存上传的视频文件
     */
    private String saveVideoFile(MultipartFile file) throws IOException, InvalidExtensionException {
        String path = Paths.get(uploadPath,"upload","file").toString();
        File targetFile = new File(path, file.getOriginalFilename());

        if (!targetFile.exists()) {
            return FileUploadUtils.upload(path, file, MimeTypeUtils.DEFAULT_ALLOWED_EXTENSION);
        }
        return targetFile.getAbsolutePath();
    }

    /**
     * 创建必要的目录结构
     */
    private void createDirectories(String baseDir, String... subDirs) throws IOException {
        for (String subDir : subDirs) {
            Files.createDirectories(Paths.get(baseDir, subDir));
        }
    }

    /**
     * 生成视频片段
     */
    private List<String> generateVideoClips(String videoPath, List<Integer> goalTimes, String baseDir)
            throws IOException, InterruptedException {

        List<String> clipPaths = new ArrayList<>();
        Path outputDir = Paths.get(baseDir, "output");

        for (int i = 0; i < goalTimes.size(); i++) {
            int goalTime = goalTimes.get(i);
            int startTime = Math.max(0, goalTime - 5);

            String clipPath = outputDir.resolve("clip_" + i + ".mp4").toString();
            clipPaths.add(clipPath);

            executeFfmpegCommand(Arrays.asList(
                    FFMPEG_PATH,
                    "-ss", String.valueOf(startTime),
                    "-t", "10", // 10秒片段
                    "-i", videoPath,
                    "-c:v", "libx264", // 使用更高效的编码器
                    "-preset", "veryfast", // 平衡速度和质量
                    "-crf", "23", // 控制质量
                    "-c:a", "aac",
                    "-y", clipPath
            ));
        }

        return clipPaths;
    }

    /**
     * 合并视频片段
     */
        private String mergeVideoClips(List<String> clipPaths, String baseDir,Integer playerId)
                throws IOException, InterruptedException {

            Path highlightsDir = Paths.get(baseDir, "highlights",playerId.toString());
            String concatFile = Paths.get(baseDir, "output", "concat.txt").toString();  //视频片段路径
            String outputPath = highlightsDir.resolve("highlight.mp4").toString();
            // 创建concat文件
            Files.write(Paths.get(concatFile),
                    clipPaths.stream().map(p -> "file '" + p + "'").collect(Collectors.toList()));
            // 执行合并命令
            executeFfmpegCommand(Arrays.asList(
                    FFMPEG_PATH,
                    "-f", "concat",
                    "-safe", "0",
                    "-i", concatFile,
                    "-c", "copy", // 使用流拷贝提高效率
                    "-y", outputPath
            ));
            return outputPath;
        }

    /**
     * 执行FFmpeg命令
     */
    private void executeFfmpegCommand(List<String> command) throws IOException, InterruptedException {
        Process process = new ProcessBuilder(command)
                .redirectErrorStream(true)
                .start();

        // 异步消费输出流
        new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    log.debug("FFmpeg: {}", line);
                }
            } catch (IOException e) {
                log.warn("读取FFmpeg输出失败", e);
            }
        }).start();

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new IOException("FFmpeg命令执行失败，退出码: " + exitCode);
        }
    }
}
