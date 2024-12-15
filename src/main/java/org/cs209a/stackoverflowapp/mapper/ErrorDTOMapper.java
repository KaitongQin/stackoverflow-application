package org.cs209a.stackoverflowapp.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.cs209a.stackoverflowapp.entity.dto.ErrorDTO;

import java.util.List;

public interface ErrorDTOMapper {
    /**
     * 获取前 N 个错误，根据加权分数排序
     *
     * @param n 需要返回的标签数量
     * @param w1 加权因子
     * @return 前 N 个错误及其加权分数和频率
     */
    @Select("""
                WITH error_stats AS (
                    SELECT
                        eo.error_type AS error_name,
                        e.severity AS error_type,
                        COUNT(q.id) AS base_frequency,            -- 标签问题的数量
                        AVG(q.view_count) AS avg_view_count       -- 标签问题的平均浏览量
                    FROM error_occurrence eo
                             JOIN question q ON eo.question_id = q.id
                             JOIN error e ON e.name = eo.error_type
                    GROUP BY eo.error_type, e.severity
                ),
                mean_std_values AS (
                    SELECT
                        AVG(base_frequency) AS mean_base_frequency,
                        STDDEV(base_frequency) AS std_base_frequency,
                        AVG(avg_view_count) AS mean_avg_view_count,
                        STDDEV(avg_view_count) AS std_avg_view_count
                    FROM error_stats
                ),
                normalized_stats AS (
                    SELECT
                        es.error_name,
                        es.error_type,
                        es.base_frequency,
                        es.avg_view_count,
                        -- Z-score归一化
                        (es.base_frequency - mv.mean_base_frequency) / NULLIF(mv.std_base_frequency, 0) AS b1,
                        (es.avg_view_count - mv.mean_avg_view_count) / NULLIF(mv.std_avg_view_count, 0) AS a1
                    FROM error_stats es, mean_std_values mv
                ),
                weighted_scores AS (
                    SELECT
                        error_name,                                        -- 标签名称
                        error_type,
                        base_frequency,                                    -- 原始基础频率
                        avg_view_count,                                    -- 原始平均浏览量
                        #{w1} * b1 + (1 - #{w1}) * a1 AS weightedScore        -- 计算加权分数
                    FROM normalized_stats
                )
                SELECT
                    error_name,
                    error_type,
                    base_frequency,
                    avg_view_count,
                    (weightedScore - MIN(weightedScore) OVER ()) / NULLIF(MAX(weightedScore) OVER () - MIN(weightedScore) OVER (), 0) AS normalizedWeightedScore  -- 归一化到[0, 1]区间
                FROM weighted_scores
                ORDER BY normalizedWeightedScore DESC;                          -- 按归一化后的加权分数降序排序

            """)
    List<ErrorDTO> getTopNJavaTopics(@Param("w1") float w1);
}
