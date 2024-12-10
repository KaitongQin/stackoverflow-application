package org.cs209a.stackoverflowapp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.cs209a.stackoverflowapp.entity.Question;
import org.cs209a.stackoverflowapp.entity.Topic;


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
        WITH tag_stats AS (
            SELECT
                qt.tag_name,
                COUNT(qt.question_id) AS base_frequency, -- 该标签问题的数量
                AVG(q.view_count) AS avg_view_count       -- 该标签问题的平均浏览量
            FROM
                question_tag qt
            JOIN
                question q ON qt.question_id = q.id
            GROUP BY
                qt.tag_name
        )
        SELECT
            tag_name AS topic, -- 标签名称映射到 Topic 类的 topic 字段
            base_frequency * (1 + #{w1} * (avg_view_count / 10000)) AS related_question_num, -- 加权分数
            base_frequency AS frequency -- 基础频率
        FROM
            tag_stats
        ORDER BY
            related_question_num DESC
        LIMIT
            #{n}
    """)
    List<Topic> getTopNJavaTopics( @Param("w1") float w1,@Param("n") int n);

    @Select("""
                 WITH tag_engagement AS (
                             SELECT
                                 qt.tag_name,
                                 COUNT(CASE WHEN t.timeline_type = 'answer' THEN 1 END) AS answer_count,
                                 COUNT(CASE WHEN t.timeline_type = 'comment' THEN 1 END) AS comment_count,
                                 COUNT(CASE WHEN t.timeline_type = 'revision' THEN 1 END) AS edit_count,
                                 COUNT(CASE WHEN t.timeline_type = 'vote_aggregate' THEN max(t.down_vote_count,0)+max(t.up_vote_count,0) END) AS vote_count,
                                 #{w1} * COUNT(CASE WHEN t.timeline_type = 'answer' THEN 1 END) +
                                 #{w2} * COUNT(CASE WHEN t.timeline_type = 'comment' THEN 1 END) +
                                 #{w3} * COUNT(CASE WHEN t.timeline_type = 'revision' THEN 1 END) +
                                 #{w4} * COUNT(CASE WHEN t.timeline_type = 'vote_aggregate' THEN max(t.down_vote_count,0)+max(t.up_vote_count,0) END) AS engagement_score
                             FROM
                                 question_tag qt
                             JOIN
                                 timeline t ON qt.question_id = t.question_id
                             GROUP BY
                                 qt.tag_name
                         ),
               high_reputation_engagement AS (
                             SELECT
                                 qt.tag_name,
                                 SUM(u.reputation * e.engagement_score) AS related_question_num,
                                 COUNT(DISTINCT t.user_id) AS total_participants
                             FROM
                                 question_tag qt
                             JOIN
                                 timeline t ON qt.question_id = t.question_id
                             JOIN
                                 "user" u ON t.user_id = u.account_id
                             JOIN
                                 tag_engagement e ON qt.tag_name = e.tag_name
                             WHERE
                                 u.reputation >= 1000
                             GROUP BY
                                 qt.tag_name
                         ),
                tag_stats AS (
                    SELECT
                        qt.tag_name,
                        COUNT(qt.question_id) AS base_frequency
                    FROM
                        question_tag qt
                    GROUP BY
                        qt.tag_name
                )
                SELECT
                    ts.tag_name AS topic,
                    ts.base_frequency * log(  (1 + (hr.related_question_num / 1000))) AS related_question_num,
                    ts.base_frequency AS frequency
                FROM
                    tag_stats ts
                JOIN
                    high_reputation_engagement hr ON ts.tag_name = hr.tag_name;
            """)
    List<Topic> getTopNParticipationTopics(int n, float w1, float w2, float w3, float w4);
}
