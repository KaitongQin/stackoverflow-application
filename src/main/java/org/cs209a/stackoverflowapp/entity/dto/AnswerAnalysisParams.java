package org.cs209a.stackoverflowapp.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnswerAnalysisParams {
    @Builder.Default
    private double scoreWeight = 0.9;  // 分数权重
    @Builder.Default
    private double acceptedWeight = 0.1;  // 采纳状态权重

    // 样本大小配置
    @Builder.Default
    private int minGroupSize = 10;  // 最小组大小
    @Builder.Default
    private int maxResults = 1000;  // 最大返回结果数

    // 分组配置
    @Builder.Default
    private int timeGapBuckets = 24;  // 时间间隔分组数
    @Builder.Default
    private int reputationBuckets = 10;  // 声望分组数
    @Builder.Default
    private int lengthBuckets = 20;  // 长度分组数

    // 过滤配置
    @Builder.Default
    private Integer minAnswerLength = 100;  // 最小答案长度
    @Builder.Default
    private Integer maxAnswerLength = 5000;  // 最大答案长度
    @Builder.Default
    private Integer maxTimeGap = 86400;  // 最大时间间隔（秒）
}
