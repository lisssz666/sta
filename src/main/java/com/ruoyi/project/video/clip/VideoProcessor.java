package com.ruoyi.project.video.clip;

import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class VideoProcessor {

    private static final Logger log = LoggerFactory.getLogger(VideoProcessor.class);

    //文件上传路径
    @Value("${spring.upload.videopath}")
    private static String uploadPath;

    //ffmpeg
    @Value("${spring.upload.ffmpeg}")
    private static String FFMPEG_PATH;

    // 生成个人集锦视频
    public static String createHighlightVideo(String videoPath, List<Integer> goalTimes) {
        try {
            log.info("FFMPEG_PATH: "+FFMPEG_PATH);
            FFmpeg ffmpeg = new FFmpeg(FFMPEG_PATH);
            FFmpegExecutor executor = new FFmpegExecutor(ffmpeg);

            log.info("ffmpeg加载完成！ "+ffmpeg);
            List<String> clipPaths = new ArrayList<>();

            // 生成视频片段
            for (int i = 0; i < goalTimes.size(); i++) {
                int goalTime = goalTimes.get(i);
                // 计算前后15秒的时间段
                int startTime = Math.max(0, goalTime - 5);
                int duration = 8; // 15秒前+15秒后

                // 创建输出路径
//                String outputPath = "/Users/lisenshuai/Desktop/李森帅专用文件夹/其他文档/上传的文件/video/output/clip_" + i + ".mp4";
//                String outputPath = "/home/web/dist/video/output/clip_" + i + ".mp4";
                String outputPath = "/home/video/output/clip_" + i + ".mp4";
                clipPaths.add(outputPath); // 将片段路径保存到列表中

                log.info("videoPath=="+videoPath);
                log.info("outputPath=="+outputPath);
                // 使用FFmpeg截取片段
                FFmpegBuilder builder = new FFmpegBuilder()
                        .setInput(videoPath)
                        .addExtraArgs("-ss", String.valueOf(startTime))
                        .addExtraArgs("-t", String.valueOf(duration))
                        .addOutput(outputPath)
                        .setVideoCodec("libx264")
                        .setAudioCodec("aac")
                        .done();
                log.info("ffmpeg加载完成2！");
//                executor.createJob(builder).run();
                // 执行合并任务并捕获输出
                try {
                    executor.createJob(builder).run();
                } catch (Exception e) {
                    log.error("Error during builder process: ", e);
                    throw e;
                }
            }

            // 合并所有视频片段
//            String outputVideo = "/Users/lisenshuai/Desktop/李森帅专用文件夹/其他文档/上传的文件/video/output/highlight.mp4";
            String outputVideo = "/home/video/highlights/highlight.mp4";
            String result = "1.12.77.209/home/video/highlights/highlight.mp4";

            // 使用concat协议合并片段
//            String concatInputFile = "/Users/lisenshuai/Desktop/李森帅专用文件夹/其他文档/上传的文件/video/output/concat.txt";
            String concatInputFile = "/home/video/output/concat.txt";
            Files.write(Paths.get(concatInputFile),
                    clipPaths.stream().map(p -> "file '" + p + "'").collect(Collectors.toList()));
            // 使用FFmpeg合并视频片段
            FFmpegBuilder mergeBuilder = new FFmpegBuilder()
                    .addExtraArgs("-f", "concat", "-safe", "0")
                    .setInput(concatInputFile)
                    .addOutput(outputVideo)
                    .setFormat("mp4")
                    .setVideoCodec("libx264")
                    .setAudioCodec("aac")
                    .done();

            log.info("ffmpeg加载完成3！");
            // 执行合并任务
//            executor.createJob(mergeBuilder).run();
            // 执行合并任务并捕获输出
            try {
                executor.createJob(mergeBuilder).run();
            } catch (Exception e) {
                log.error("Error during mergeBuilder process: ", e);
                throw e;
            }

            return result;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


}
