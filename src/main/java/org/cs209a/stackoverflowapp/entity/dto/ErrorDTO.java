package org.cs209a.stackoverflowapp.entity.dto;

import lombok.Data;

@Data
public class ErrorDTO {
    private String errorName; // 错误名称
    private String errorType; //
    private Float baseFrequency; // 基础频率
    private Float normalizedWeightedScore; // 加权后分数
    private Float avgViewCount; // 平均浏览次数
}
