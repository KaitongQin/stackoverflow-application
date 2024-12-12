package org.cs209a.stackoverflowapp.mapper;

import org.apache.ibatis.annotations.Select;
import org.cs209a.stackoverflowapp.entity.dto.ParticipationDTO;

import java.util.List;

public interface ParticipationDTOMapper {
    @Select("""
            WITH tag_engagement AS (SELECT qt.tag_name,
                                                         COUNT(CASE WHEN t.timeline_type = 'answer' THEN 1 END)                                      AS answer_count,
                                                         COUNT(CASE WHEN t.timeline_type = 'comment' THEN 1 END)                                     AS comment_count,
                                                         COUNT(CASE WHEN t.timeline_type = 'revision' THEN 1 END)                                    AS edit_count,
                                                         COUNT(CASE
                                                                   WHEN t.timeline_type = 'vote_aggregate'
                                                                       THEN GREATEST(t.down_vote_count, 0) + GREATEST(t.up_vote_count, 0) END)       AS vote_count,
                                                         #{w1} * COUNT(CASE WHEN t.timeline_type = 'answer' THEN 1 END) +
                                                         #{w2} * COUNT(CASE WHEN t.timeline_type = 'comment' THEN 1 END) +
                                                         #{w3} * COUNT(CASE WHEN t.timeline_type = 'revision' THEN 1 END) +
                                                         #{w4} * COUNT(CASE
                                                                         WHEN t.timeline_type = 'vote_aggregate'
                                                                             THEN GREATEST(t.down_vote_count, 0) + GREATEST(t.up_vote_count, 0) END) AS engagement_score
                                                  FROM question_tag qt
                                                           JOIN
                                                       timeline t ON qt.question_id = t.question_id
                                                  GROUP BY qt.tag_name),
                               high_reputation_engagement AS (SELECT qt.tag_name,
                                                                     SUM(log(u.reputation) * e.engagement_score) /
                                                                     COUNT(DISTINCT t.user_id) AS weight,
                                                                     COUNT(DISTINCT t.user_id) AS total_participants
                                                              FROM question_tag qt
                                                                       JOIN
                                                                   timeline t ON qt.question_id = t.question_id
                                                                       JOIN
                                                                   "user" u ON t.user_id = u.account_id
                                                                       JOIN
                                                                   tag_engagement e ON qt.tag_name = e.tag_name
                                                              WHERE u.reputation >= 1000
                                                              GROUP BY qt.tag_name),
                               tag_stats AS (SELECT qt.tag_name,
                                                    COUNT(qt.question_id) AS base_frequency
                                             FROM question_tag qt
                                             GROUP BY qt.tag_name)
                          SELECT ts.tag_name                                  AS tag_name,
                                 ts.base_frequency * (1 + (hr.weight / 1000)) AS weightedScore,
                                 hr.weight                                    AS avg_view_count,
                                 ts.base_frequency                            AS base_frequency
                          FROM tag_stats ts
                                   JOIN
                               high_reputation_engagement hr ON ts.tag_name = hr.tag_name
                          ORDER BY weightedScore DESC
                          OFFSET 1 LIMIT #{n}
                       
            """)
    List<ParticipationDTO> getTopNParticipationTopics(int n, float w1, float w2, float w3, float w4);
}
