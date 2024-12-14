package org.cs209a.stackoverflowapp.mapper;

import org.apache.ibatis.annotations.Select;
import org.cs209a.stackoverflowapp.entity.dto.ParticipationDTO;

import java.util.List;

public interface ParticipationDTOMapper {
    @Select("""
            WITH aggregated_timeline AS (
                SELECT
                    qt.tag_name,
                    qt.question_id,
                    SUM(CASE WHEN t.timeline_type = 'answer' THEN 1 ELSE 0 END) AS answer_num,
                    SUM(CASE WHEN t.timeline_type = 'comment' THEN 1 ELSE 0 END) AS comment_num,
                    SUM(CASE WHEN t.timeline_type = 'revision' THEN 1 ELSE 0 END) AS revision_num,
                    SUM(CASE WHEN t.timeline_type = 'vote_aggregate' THEN GREATEST(t.down_vote_count, 0) + GREATEST(t.up_vote_count, 0) ELSE 0 END) AS vote_num
                FROM question_tag qt
                         JOIN timeline t ON qt.question_id = t.question_id
                GROUP BY qt.tag_name, qt.question_id
            ),
                 overall_user_engagement AS (
                     SELECT
                         tag_name,
                         COUNT(DISTINCT question_id) AS question_num,
                         SUM(answer_num) AS answer_num,
                         SUM(comment_num) AS comment_num,
                         SUM(revision_num) AS revision_num,
                         SUM(vote_num) AS vote_num,
                         (5 * SUM(answer_num) + 2 * SUM(comment_num) + 2 * SUM(revision_num) + SUM(vote_num))
                             / COUNT(DISTINCT question_id) AS engagement_score
                     FROM aggregated_timeline
                     GROUP BY tag_name
                     HAVING COUNT(DISTINCT question_id) > 30
                 ),
                 high_reputation_user_engagement AS (
                     SELECT
                         qt.tag_name,
                         COUNT(DISTINCT qt.question_id) AS question_num,
                         SUM(CASE WHEN t.timeline_type = 'answer' THEN 1 ELSE 0 END) AS high_reputation_answer_num,
                         SUM(CASE WHEN t.timeline_type = 'comment' THEN 1 ELSE 0 END) AS high_reputation_comment_num,
                         SUM(CASE WHEN t.timeline_type = 'revision' THEN 1 ELSE 0 END) AS high_reputation_revision_num,
                         SUM(CASE WHEN t.timeline_type = 'vote_aggregate' THEN GREATEST(t.down_vote_count, 0) + GREATEST(t.up_vote_count, 0) ELSE 0 END) AS high_reputation_vote_num,
                         (5 * SUM(CASE WHEN t.timeline_type = 'answer' THEN 1 ELSE 0 END) +
                          2 * SUM(CASE WHEN t.timeline_type = 'comment' THEN 1 ELSE 0 END) +
                          2 * SUM(CASE WHEN t.timeline_type = 'revision' THEN 1 ELSE 0 END) +
                          SUM(CASE WHEN t.timeline_type = 'vote_aggregate' THEN GREATEST(t.down_vote_count, 0) + GREATEST(t.up_vote_count, 0) ELSE 0 END))
                             / COUNT(DISTINCT qt.question_id) AS high_reputation_engagement_score
                     FROM question_tag qt
                              JOIN timeline t ON qt.question_id = t.question_id
                              JOIN "user" u ON t.user_id = u.account_id
                     WHERE u.reputation > #{R}
                     GROUP BY qt.tag_name
                     HAVING COUNT(DISTINCT qt.question_id) > 30
                 ),
                 top_20_engagement AS (
                     SELECT
                                 ROW_NUMBER() OVER (ORDER BY engagement_score DESC) AS row_num,
                                 tag_name,
                                 answer_num,
                                 comment_num,
                                 revision_num,
                                 vote_num,
                                 engagement_score
                     FROM overall_user_engagement
                     LIMIT #{n}
                 ),
                 top_20_high_rep AS (
                     SELECT
                                 ROW_NUMBER() OVER (ORDER BY high_reputation_engagement_score DESC) AS row_num,
                                 tag_name AS high_reputation_tag_name,
                                 high_reputation_answer_num,
                                 high_reputation_comment_num,
                                 high_reputation_revision_num,
                                 high_reputation_vote_num,
                                 high_reputation_engagement_score
                     FROM high_reputation_user_engagement
                     LIMIT #{n}
                 )
            SELECT
                e.tag_name,
                e.answer_num,
                e.comment_num,
                e.revision_num,
                e.vote_num,
                e.engagement_score,
                hr.high_reputation_tag_name,
                hr.high_reputation_answer_num,
                hr.high_reputation_comment_num,
                hr.high_reputation_revision_num,
                hr.high_reputation_vote_num,
                hr.high_reputation_engagement_score
            FROM top_20_engagement e
                     JOIN top_20_high_rep hr
                          ON e.row_num = hr.row_num
            ORDER BY e.row_num;
            """)
    List<ParticipationDTO> getTopNParticipationTopics(int n, int R);
}
