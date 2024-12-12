package org.cs209a.stackoverflowapp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.cs209a.stackoverflowapp.controller.TopicController;
import org.cs209a.stackoverflowapp.entity.Question;
import org.cs209a.stackoverflowapp.entity.dto.TopicDTO;


import java.util.List;


public interface QuestionMapper extends BaseMapper<Question> {

    /**
     * 获取前 N 个标签，根据加权分数排序
     *
     * @param n 需要返回的标签数量
     * @param w1 加权因子
     * @return 前 N 个标签及其加权分数和频率
     */
    @Select("""
        WITH tag_stats AS (SELECT qt.tag_name,
                                          COUNT(qt.question_id) AS base_frequency, -- 该标签问题的数量
                                          AVG(q.view_count)     AS avg_view_count  -- 该标签问题的平均浏览量
                                   FROM question_tag qt
                                            JOIN
                                        question q ON qt.question_id = q.id
                                   GROUP BY qt.tag_name)
                SELECT tag_name,                                                                -- 标签名称
                       base_frequency,                                                          -- 基础频率
                       avg_view_count,                                                          -- 平均浏览量
                       base_frequency * (1 + #{w1} * (avg_view_count / 10000)) AS weightedScore -- 加权分数
                FROM tag_stats
                ORDER BY weightedScore DESC
                OFFSET 1
                LIMIT #{n}
    """)
    List<TopicDTO> getTopNJavaTopics(@Param("w1") float w1, @Param("n") int n);

}
