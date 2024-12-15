package org.cs209a.stackoverflowapp.mapper;

import org.apache.ibatis.annotations.*;
import org.cs209a.stackoverflowapp.entity.dto.AnswerAnalysisParams;
import org.cs209a.stackoverflowapp.entity.dto.AnswerQualityDTO.GroupStats;

import java.util.List;
import java.util.Map;

public interface AnswerQualityDTOMapper {
    @Update("DROP TABLE IF EXISTS temp_answer_metrics")
    void dropTempTable();

    @SelectProvider(type = SqlProvider.class, method = "createTempTableQuery")
    void createTempTable(@Param("params") AnswerAnalysisParams params);

    @Select("""
            SELECT
                COUNT(*) as total_answers,
                ROUND(AVG(normalized_score)::numeric, 4) as avg_quality_score,
                COUNT(CASE WHEN is_accepted THEN 1 END) as accepted_answers_count,
                ROUND(CORR(time_gap, normalized_score)::numeric, 4) as time_gap_correlation,
                ROUND(CORR(user_reputation, normalized_score)::numeric, 4) as reputation_correlation,
                ROUND(CORR(answer_length, normalized_score)::numeric, 4) as length_correlation
            FROM temp_answer_metrics
            """)
    Map<String, Object> getBasicStats();

    @Select("""
            SELECT
                width_bucket(time_gap, 0, #{params.maxTimeGap}, #{params.timeGapBuckets}) as bucket,
                MIN(time_gap)::float as range_start,
                MAX(time_gap)::float as range_end,
                ROUND(AVG(normalized_score)::numeric, 4) as avg_quality_score,
                COUNT(*) as count,
                ROUND(AVG(CASE WHEN is_accepted THEN 1.0 ELSE 0.0 END)::numeric, 4) as accepted_rate
            FROM temp_answer_metrics
            GROUP BY bucket
            HAVING COUNT(*) >= #{params.minGroupSize}
            ORDER BY bucket
            """)
    List<GroupStats> getTimeGapGroups(@Param("params") AnswerAnalysisParams params);

    @Select("""
            SELECT
                width_bucket(user_reputation, 0,
                    (SELECT MAX(user_reputation) FROM temp_answer_metrics),
                    #{params.reputationBuckets}) as bucket,
                MIN(user_reputation)::float as range_start,
                MAX(user_reputation)::float as range_end,
                ROUND(AVG(normalized_score)::numeric, 4) as avg_quality_score,
                COUNT(*) as count,
                ROUND(AVG(CASE WHEN is_accepted THEN 1.0 ELSE 0.0 END)::numeric, 4) as accepted_rate
            FROM temp_answer_metrics
            GROUP BY bucket
            HAVING COUNT(*) >= #{params.minGroupSize}
            ORDER BY bucket
            """)
    List<GroupStats> getReputationGroups(@Param("params") AnswerAnalysisParams params);

    @Select("""
            SELECT
                width_bucket(answer_length, 0,
                    (SELECT MAX(answer_length) FROM temp_answer_metrics),
                    #{params.lengthBuckets}) as bucket,
                MIN(answer_length)::float as range_start,
                MAX(answer_length)::float as range_end,
                ROUND(AVG(normalized_score)::numeric, 4) as avg_quality_score,
                COUNT(*) as count,
                ROUND(AVG(CASE WHEN is_accepted THEN 1.0 ELSE 0.0 END)::numeric, 4) as accepted_rate
            FROM temp_answer_metrics
            GROUP BY bucket
            HAVING COUNT(*) >= #{params.minGroupSize}
            ORDER BY bucket
            """)
    List<GroupStats> getLengthGroups(@Param("params") AnswerAnalysisParams params);

    class SqlProvider {
        public String createTempTableQuery(AnswerAnalysisParams params) {
            return """
                    CREATE TEMPORARY TABLE temp_answer_metrics AS (
                        SELECT
                            a.id AS answer_id,
                            a.question_id,
                            a.score,
                            a.is_accepted,
                            EXTRACT(EPOCH FROM (a.creation_date - q.creation_date)) AS time_gap,
                            a.body_length answer_length,
                            (u.reputation) AS user_reputation,
                            (CASE
                                WHEN MAX(a.score) OVER (PARTITION BY a.question_id) = MIN(a.score) OVER (PARTITION BY a.question_id) THEN 1.0
                                ELSE (a.score - MIN(a.score) OVER (PARTITION BY a.question_id))::float /
                                     NULLIF((MAX(a.score) OVER (PARTITION BY a.question_id) - MIN(a.score) OVER (PARTITION BY a.question_id)), 0)
                            END * #{params.scoreWeight} +
                            CASE WHEN a.is_accepted THEN #{params.acceptedWeight} ELSE 0 END) AS normalized_score
                        FROM answer a
                        INNER JOIN question q ON a.question_id = q.id
                        INNER JOIN "user" u ON a.owner_id = u.account_id
                        WHERE
                            CASE WHEN #{params.minAnswerLength} IS NOT NULL
                                THEN a.body_length >= #{params.minAnswerLength}
                                ELSE true END
                            AND
                            CASE WHEN #{params.maxAnswerLength} IS NOT NULL
                                THEN a.body_length <= #{params.maxAnswerLength} 
                                ELSE true END
                            AND CASE WHEN #{params.maxTimeGap} IS NOT NULL 
                                THEN EXTRACT(EPOCH FROM (a.creation_date - q.creation_date)) <= #{params.maxTimeGap} 
                                ELSE true END
                        ORDER BY a.creation_date DESC
                        LIMIT #{params.maxResults}
                    )
                    """;
        }
    }
}