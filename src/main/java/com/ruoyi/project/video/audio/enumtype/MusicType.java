package com.ruoyi.project.video.audio.enumtype;

import java.util.regex.Pattern;

public enum MusicType {
    OFFENSE(1, ".*进攻.*"),
    DEFENSE(2, ".*防守.*"),
    WARM_UP(3, ".*暖场.*"),
    ENTER(4, ".*入场.*"),
    SCORE(5, ".*得分.*"),
    ATMOSPHERE(6, ".*气氛.*"),
    ANTHEM(7, ".*国歌.*");

    public final int code;
    public final Pattern pattern;

    MusicType(int code, String regex) {
        this.code = code;
        this.pattern = Pattern.compile(regex);
    }
}