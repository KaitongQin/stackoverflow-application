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
    // 分数权重 * 归一化分数 + 采纳状态权重 * is被采纳 = 最终分数
    // 分数权重 + 采纳状态权重 = 1
    @Builder.Default
    private double scoreWeight = 0.9;  // 分数权重
    @Builder.Default
    private double acceptedWeight = 0.1;  // 采纳状态权重

    // 样本大小配置
    @Builder.Default
    private int minGroupSize = 10;  // 最小组大小，如果一个组内样本数量小于该值，则去除这一组
    @Builder.Default
    private int maxResults = 1000;  // 最大返回结果数，只返回按照时间排序的前 maxResults 个结果

    // 分组配置
    @Builder.Default
    private int timeGapBuckets = 24;  // 时间间隔分组数，
    @Builder.Default
    private int reputationBuckets = 10;  // 声望分组数
    @Builder.Default
    private int lengthBuckets = 20;  // 长度分组数

    // 过滤配置
    @Builder.Default
    private Integer minAnswerLength = 100;  // 最小答案长度，如果答案长度小于该值，将其移除样本空间
    @Builder.Default
    private Integer maxAnswerLength = 5000;  // 最大答案长度，如果答案长度大于该值，将其移除样本空间
    @Builder.Default
    private Integer maxTimeGap = 86400;  // 最大时间间隔（秒），如果从提问到回答的时间间隔大于该值，将其移除样本空间
}
