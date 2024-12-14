package org.cs209a.stackoverflowapp.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.cs209a.stackoverflowapp.entity.dto.TopicDTO;

import java.util.List;

public interface TopicDTOMapper {
    /**
     * 获取前 N 个标签，根据加权分数排序
     *
     * @param n 需要返回的标签数量
     * @param w1 加权因子
     * @return 前 N 个标签及其加权分数和频率
     */
    @Select("""
        WITH tag_stats AS (
            SELECT
                qt.tag_name,
                COUNT(qt.question_id) AS base_frequency, -- 该标签问题的数量
                AVG(q.view_count) AS avg_view_count      -- 该标签问题的平均浏览量
            FROM
                question_tag qt
                    JOIN question q ON qt.question_id = q.id
            GROUP BY
                qt.tag_name
            ORDER BY
                base_frequency DESC
            LIMIT
                70
                OFFSET
                1
        ),
             normalized_stats AS (
                 SELECT
                     tag_name,
                     base_frequency,
                     avg_view_count,
                     -- z-score 归一化 base_frequency
                     (base_frequency - AVG(base_frequency) OVER()) / NULLIF(STDDEV(base_frequency) OVER(), 0) AS normalized_frequency,
                     -- z-score 归一化 avg_view_count
                     (avg_view_count - AVG(avg_view_count) OVER()) / NULLIF(STDDEV(avg_view_count) OVER(), 0) AS normalized_view_count
                 FROM
                     tag_stats
             ),
             weighted_scores AS (
                 SELECT
                     tag_name,
                     base_frequency,
                     avg_view_count,
                     normalized_frequency,
                     normalized_view_count,
                     -- 严格按照 Python 加权公式对齐
                     #{w} * normalized_frequency + (1 - #{w}) * normalized_view_count AS weighted_score
                 FROM
                     normalized_stats
             )
        SELECT
            tag_name,
            base_frequency,
            avg_view_count,
            weighted_score
        FROM
            weighted_scores
        ORDER BY
            weighted_score DESC;
    """)
    List<TopicDTO> getTopNJavaTopics(@Param("w1") float w1);
}
