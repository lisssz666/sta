package com.ruoyi.project.video.clip;

import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.common.utils.file.MimeTypeUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/video")
public class VideoController {

    //文件上传路径
    @Value("${spring.upload.videopath}")
    private String uploadPath;

    @PostMapping("/process")
    public String processVideo(@RequestParam("file") MultipartFile file,
                               @RequestParam("goalTimes") List<Integer> goalTimes) {
        // 将视频保存到本地
        String videoPath = saveFile(file);

        // 调用视频处理方法
        String highlightVideoPath = processGoalsInVideo(videoPath, goalTimes);

        return "Highlight video created at: " + highlightVideoPath;
    }

    private String saveFile(MultipartFile file) {
        // 保存视频文件到本地
        // 这里省略了具体的保存代码
        try {
            String result = FileUploadUtils.upload(uploadPath, file, MimeTypeUtils.DEFAULT_ALLOWED_EXTENSION);
            ///Users/lisenshuai/Desktop/李森帅专用文件夹/其他文档/上传的文件/2024/09/11/cc891d80-8c94-4b48-af9f-abcb057cb923.mp4
            return result;
        }catch (Exception e)
        {
        }
        return null;
    }

    private String processGoalsInVideo(String videoPath, List<Integer> goalTimes) {
        // 调用FFmpeg来处理视频
        return VideoProcessor.createHighlightVideo(videoPath, goalTimes);
    }
}
