package org.cs209a.stackoverflowapp.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.cs209a.stackoverflowapp.entity.dto.ErrorDTO;

import java.util.List;

public interface ErrorDTOMapper {
    /**
     * 获取前 N 个错误，根据加权分数排序
     *
     * @param w1 加权因子
     * @return 前 N 个错误及其加权分数和频率
     */
    @Select("""
                WITH base_error_counts AS (
                     SELECT\s
                         error_type,\s
                         COUNT(*) AS base_frequency
                     FROM error_occurrence
                     GROUP BY error_type
                 ),
                 error_stats AS (
                     SELECT
                         eo.error_type AS error_name,
                         e.severity AS error_type,
                         bec.base_frequency,  -- 引用子查询的结果
                         AVG(q.view_count) AS avg_view_count
                     FROM error_occurrence eo
                              JOIN question q ON eo.question_id = q.id
                              JOIN error e ON e.name = eo.error_type
                              JOIN base_error_counts bec ON eo.error_type = bec.error_type
                     GROUP BY eo.error_type, e.severity, bec.base_frequency
                 )
                SELECT
                    es.error_name,
                    es.error_type,
                    es.base_frequency,
                    es.avg_view_count,
                    -- Z-score归一化
                    (#{w1} * ((es.base_frequency - mv.mean_base_frequency) / NULLIF(mv.std_base_frequency, 0)) +
                     (1 - #{w1}) * ((es.avg_view_count - mv.mean_avg_view_count) / NULLIF(mv.std_avg_view_count, 0))) AS weightedScore,
                    -- 归一化到 [0, 1] 区间
                    ( (#{w1} * ((es.base_frequency - mv.mean_base_frequency) / NULLIF(mv.std_base_frequency, 0)) +
                        (1 - #{w1}) * ((es.avg_view_count - mv.mean_avg_view_count) / NULLIF(mv.std_avg_view_count, 0)))\s
                        - MIN(#{w1} * ((es.base_frequency - mv.mean_base_frequency) / NULLIF(mv.std_base_frequency, 0)) +
                              (1 - #{w1}) * ((es.avg_view_count - mv.mean_avg_view_count) / NULLIF(mv.std_avg_view_count, 0))) OVER ()
                    ) / NULLIF(
                        MAX(#{w1} * ((es.base_frequency - mv.mean_base_frequency) / NULLIF(mv.std_base_frequency, 0)) +
                            (1 - #{w1}) * ((es.avg_view_count - mv.mean_avg_view_count) / NULLIF(mv.std_avg_view_count, 0))) OVER () -
                        MIN(#{w1} * ((es.base_frequency - mv.mean_base_frequency) / NULLIF(mv.std_base_frequency, 0)) +
                            (1 - #{w1}) * ((es.avg_view_count - mv.mean_avg_view_count) / NULLIF(mv.std_avg_view_count, 0))) OVER (), 0
                    ) AS normalizedWeightedScore
                FROM error_stats es
                CROSS JOIN (
                    SELECT
                        AVG(base_frequency) AS mean_base_frequency,
                        STDDEV(base_frequency) AS std_base_frequency,
                        AVG(avg_view_count) AS mean_avg_view_count,
                        STDDEV(avg_view_count) AS std_avg_view_count
                    FROM error_stats
                ) mv
                ORDER BY normalizedWeightedScore DESC;
            """)
    List<ErrorDTO> getTopNJavaTopics(@Param("w1") float w1);
}
