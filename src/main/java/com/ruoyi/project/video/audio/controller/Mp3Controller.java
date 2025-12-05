package com.ruoyi.project.video.audio.controller;

import com.ruoyi.framework.web.domain.AjaxResult;
import com.ruoyi.project.video.audio.enumtype.MusicType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/audio")
public class Mp3Controller {

    // 1. 定义根目录：服务器上 MP3 文件存放路径
    private static final Path ROOT = Paths.get("/home/web/dist/yinbo/mp3");
//    private static final Path ROOT = Paths.get("D:\\BaiduNetdiskDownload\\篮球比赛-精选音乐\\篮球比赛-精选音乐\\3 5V5进攻防守\\1进攻");

    /**
     * 2. GET /audio/list
     * 一次性返回目录下所有 MP3 文件，按文件名排序
     */
    @GetMapping("/list")
    public AjaxResult list() {
        try (Stream<Path> stream = Files.walk(ROOT, 1)) {
            List<Map<String, Object>> list = stream
                    // 4-1 只保留 *.mp3
                    .filter(p -> p.toString().toLowerCase().endsWith(".mp3"))
                    // 4-2 按文件名自然排序
                    .sorted((p1, p2) -> p1.getFileName().toString()
                            .compareToIgnoreCase(p2.getFileName().toString()))
                    // 4-3 映射成 Map（name + url + type）
                    .map(p -> {
                        String name = p.getFileName().toString();
                        String nameNoExt = name.endsWith(".mp3") ? name.substring(0, name.length() - 4) : name;
                        String encodedName = null;
                        try {
                            encodedName = URLEncoder.encode(name, StandardCharsets.UTF_8.toString())
                                    .replace("+", "%20");// URLEncoder 会把空格变成 +，这里手动改回 %20
                        } catch (UnsupportedEncodingException e) {
                            throw new RuntimeException(e);
                        }

                        String url = "http://114.132.162.213/home/web/dist/yinbo/mp3/" + encodedName;
//                        String url = "D:\\BaiduNetdiskDownload\\篮球比赛-精选音乐\\篮球比赛-精选音乐\\3 5V5进攻防守\\1进攻\\" + encodedName;
                        // 根据文件名匹配分类码
                        int type = Arrays.stream(MusicType.values())
                                .filter(t -> t.pattern.matcher(nameNoExt).find())
                                .findFirst()
                                .map(t -> t.code)
                                .orElse(0);  // 0 = 未知

                        Map<String, Object> map = new HashMap<>();
                        map.put("name", nameNoExt);
                        map.put("url", url);
                        map.put("type", type);   // 分类码
                        return map;
                    })
                    .collect(Collectors.toList());

            return AjaxResult.success(list);
        } catch (IOException e) {
            return AjaxResult.error("读取文件失败");
        }
    }
}