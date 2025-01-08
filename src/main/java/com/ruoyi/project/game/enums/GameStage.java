package com.ruoyi.project.game.enums;

/**
 * 操作状态
 * 
 * @author ruoyi
 *
 */
public enum GameStage
{
    GROUP_STAGE("小组赛"),
    KNOCKOUT_STAGE("淘汰赛"),
    SEMIFINAL("半决赛"),
    FINAL("决赛"),
    CHAMPIONSHIP("总决赛");

    private final String description;

    GameStage(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
