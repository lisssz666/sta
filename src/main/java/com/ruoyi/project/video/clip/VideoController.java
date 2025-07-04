package com.ruoyi.project.video.clip;

import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.common.utils.file.MimeTypeUtils;
import com.ruoyi.project.video.mapper.StaVideoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/video")
public class VideoController {

    @Autowired
    private VideoProcessor videoProcessor;

    @PostMapping("/process")
    public String processVideo(@RequestParam("file") MultipartFile file,
                               @RequestParam("goalTimes") List<Integer> goalTimes) {
        // 调用视频处理方法
        String highlightVideoPath = processGoalsInVideo(file, goalTimes);
        return "Highlight video created at: " + highlightVideoPath;
    }

    //上传文件-生成个人集锦
    private String processGoalsInVideo(MultipartFile file, List<Integer> goalTimes) {
        // 调用FFmpeg来处理视频
        return videoProcessor.createHighlightUploadVideo(file, goalTimes,99);
    }
}
