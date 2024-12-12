package org.cs209a.stackoverflowapp.entity.dto;

import lombok.Data;

@Data
public class ParticipationDTO {
    private String tagName; // 话题名称
    private Float baseFrequency; // 基础频率
    private Float weightedScore; // 加权后分数
    private Float avgViewCount; // 平均浏览次数
}
