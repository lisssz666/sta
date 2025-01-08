package com.ruoyi.project.statistic.domain;

import lombok.Data;

/**
 * 球员统计信息实体类
 */

@Data
public class StaBestOfGame {

    /**
     * 统计项
     */
    private String staItem;

    /**
     * 主队球员姓名
     */
    private String homePlayerName;

    /**
     * 主队球员号码
     */
    private String homePlayerNumber;

    /**
     * 主队数值
     */
    private String homeValue;

    /**
     * 客队球员姓名
     */
    private String awayPlayerName;

    /**
     * 客队球员号码
     */
    private String awayPlayerNumber;

    /**
     * 客队数值
     */
    private String awayValue;
}

