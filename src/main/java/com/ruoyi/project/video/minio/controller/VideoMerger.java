package com.ruoyi.project.video.minio.controller;

import com.ruoyi.framework.web.domain.AjaxResult;
import com.ruoyi.project.video.minio.config.MinioConfig;
import io.minio.MinioClient;
import io.minio.GetObjectArgs;
import io.minio.Result;
import io.minio.messages.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

@Service
public class VideoMerger {

    @Autowired
    MinioConfig minioConfig;

    private static String DOWNLOAD_VIDEO;  // 存储minio上下载的视频目录
    private static String FFMPEG_PATH;   //安装的ffmpeg目录
    private static String FILE_LIST_PATH;   // 创建临时文件用于存储视频列表


    //从minio下载目录
    @Value("${spring.upload.videopath}")
    private String  videoPath;

    //ffmpeg目录
    @Value("${spring.upload.ffmpeg}")
    private String ffmpegPath;

    @Value(value = "${minio.bucket}")
    private String BUCKET_NAME;

    @Value(value = "${minio.host}")
    private String MINIO_URL;

    @Value(value = "${minio.access-key}")
    private String ACCESS_KEY;

    @Value(value = "${minio.secret-key}")
    private String SECRET_KEY;


    @PostConstruct
    public void init() {
        DOWNLOAD_VIDEO = videoPath;
        FFMPEG_PATH = ffmpegPath;
        FILE_LIST_PATH = DOWNLOAD_VIDEO + "/filelist.txt";
    }
    //拼接目录
    private static String DIRECTORY_PATH = null;  //"league"+leagueId+"/game"+gameId+"/";

    //合并所有小视频到一个视频
    public AjaxResult fullPlayBlack(Integer leagueId, Integer gameId ) {
        try {
            MinioClient minioClient = MinioClient.builder()
                    .endpoint(MINIO_URL)
                    .credentials(ACCESS_KEY, SECRET_KEY)
                    .build();

            DIRECTORY_PATH = "league"+leagueId+"/game"+gameId;
            // List of video files to merge
            ArrayList<String> videoFiles = listVideoFilesInDirectory(minioClient, BUCKET_NAME, DIRECTORY_PATH +"/video/");

            String reMergerVideo = null;
            if (!videoFiles.isEmpty()) {
                // Download videos from MinIO
                for (String videoFile : videoFiles) {
                    downloadVideo(minioClient, BUCKET_NAME, videoFile);
                    System.out.print("视频全部下载完成！！");
                }

                // Merge videos using FFmpeg
                mergeVideos(videoFiles);

                // Upload merged video back to MinIO
                reMergerVideo = uploadVideo(leagueId, gameId);
                return AjaxResult.success("视频合并完成，已上传服务器",reMergerVideo);
            }else {
                return AjaxResult.success("没有视频文件",reMergerVideo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return AjaxResult.error("出现问题，请联系管理员");
    }

    private static ArrayList<String> listVideoFilesInDirectory(MinioClient minioClient, String bucketName, String directoryPath) throws Exception {
        ArrayList<String> videoFiles = new ArrayList<>();
        Iterable<Result<Item>> results = minioClient.listObjects(bucketName, directoryPath, true);
        for (Result<Item> result : results) {
            Item item = result.get();
            String objectName = item.objectName();
            if (objectName.endsWith(".mp4")) { // Assuming all videos are in mp4 format
                videoFiles.add(objectName);
            }
        }
        return videoFiles;
    }

    private static void downloadVideo(MinioClient minioClient, String bucketName, String objectName) throws Exception {
        try (InputStream stream = minioClient.getObject(GetObjectArgs.builder().bucket(bucketName).object(objectName).build())) {
            objectName = DOWNLOAD_VIDEO + objectName;  //objectName :league1/game1/403_1732089391.mp4
            File file = new File(objectName);
            File parentDir = file.getParentFile();
            if (!parentDir.exists()) {
                if (!parentDir.mkdirs()) {
                    throw new Exception("Failed to create directories: " + parentDir.getAbsolutePath());
                }
            }
            try (FileOutputStream fos = new FileOutputStream(file)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = stream.read(buffer)) != -1) {
                    fos.write(buffer, 0, bytesRead);
                }
            }
        }
    }

    private static void mergeVideos( ArrayList<String> localVideoFiles) throws Exception {
        // 获取目录下的所有视频文件
        String videoFiles2 = DOWNLOAD_VIDEO + DIRECTORY_PATH +"/video";  // DOWNLOAD_VIDEO: /Users/lisenshuai/Desktop/李森帅专用文件夹/视频/篮球视频/测试合成视频/   DIRECTORY_PATH: league1/game1/
        File directory = new File(videoFiles2);
        File[] videoFiles = directory.listFiles((dir, name) -> name.endsWith(".mp4"));
        if (videoFiles == null || videoFiles.length == 0) {
            System.out.println("没有找到视频文件");
            return;
        }
        // 按文件名排序（可选）
        Arrays.sort(videoFiles);
        try (PrintWriter writer = new PrintWriter(FILE_LIST_PATH)) {
            for (String video : localVideoFiles) {
                writer.println("file '" + DOWNLOAD_VIDEO+video + "'");
            }
        }
        //合并存放的路径
        String outputVideoPath = DOWNLOAD_VIDEO +DIRECTORY_PATH+ "/merger/merged_video.mp4";
        //如果没有存放合并文件夹就创建一个
        File file = new File(outputVideoPath);
        File parentDir = file.getParentFile();
        if (!parentDir.exists()) {
            if (!parentDir.mkdirs()) {
                throw new Exception("Failed to create directories: " + parentDir.getAbsolutePath());
            }
        }
        // 构建ffmpeg命令
        String cmd = FFMPEG_PATH + " -f concat -safe 0 -i " + FILE_LIST_PATH + " -c copy -y " + outputVideoPath;
        try {
            Process process = Runtime.getRuntime().exec(cmd);
            // 获取ffmpeg的输出，以便于调试
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            process.waitFor();
            System.out.println("视频合并完成！");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }finally {
            // 删除临时的filelist.txt文件
//            if (fileList.exists()) {
//                fileList.delete();
//            }
        }
    }
    /*private static void uploadVideo(MinioClient minioClient, String bucketName, String objectName) throws Exception {
        try (InputStream stream = Paths.get(objectName).toUri().toURL().openStream()) {
            minioClient.putObject(PutObjectArgs.builder().bucket(bucketName).object(objectName).stream(stream, -1, 10485760).build());
        }
    }*/
    private String uploadVideo(Integer leagueId,Integer gameId) throws Exception {
        //合并存放的路径
        String outputVideoPath = DOWNLOAD_VIDEO +DIRECTORY_PATH+ "/merger/merged_video.mp4";
        File file = new File(outputVideoPath);
        MultipartFile multipartFile = convert(file);
        // 将 leagueId 和 gameId 转换为字符串
        String leagueIdStr = String.valueOf(leagueId);
        String gameIdStr = String.valueOf(gameId);
        // 上传
        String mergerVideo = minioConfig.putObject(multipartFile, leagueIdStr, gameIdStr, 1);
        return mergerVideo;
    }

    //file转换成MultipartFile
    public static MultipartFile convert(File file) throws IOException {
        FileInputStream input = new FileInputStream(file);
        MultipartFile multipartFile = new MockMultipartFile("file",
                file.getName(), "text/plain", input);
        return multipartFile;
    }

}
