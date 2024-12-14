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
                WITH error_stats AS (SELECT eo.error_type as error_name,
                                            e.severity as error_type,
                                            COUNT(q.id) AS base_frequency, -- 该标签问题的数量
                                            AVG(q.view_count) AS avg_view_count  -- 该标签问题的平均浏览量
                                           FROM error_occurrence eo
                                                    JOIN
                                                question q ON eo.question_id = q.id
                                                    JOIN
                                                error e ON e.name = eo.error_type
                                           GROUP BY eo.error_type,e.severity)
                        SELECT error_name,                                                                -- 标签名称
                               error_type,
                               base_frequency,                                                          -- 基础频率
                               avg_view_count,                                                          -- 平均浏览量
                               base_frequency * (1 + #{w1} * (avg_view_count / 10000)) AS weightedScore -- 加权分数
                        FROM error_stats
                        ORDER BY weightedScore DESC
            """)
    List<ErrorDTO> getTopNJavaTopics(@Param("w1") float w1);
}
