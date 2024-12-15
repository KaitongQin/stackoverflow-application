package org.cs209a.stackoverflowapp.service;

import org.apache.commons.math3.distribution.TDistribution;
import org.cs209a.stackoverflowapp.entity.dto.AnswerAnalysisParams;
import org.cs209a.stackoverflowapp.entity.dto.AnswerQualityDTO;
import org.cs209a.stackoverflowapp.entity.dto.AnswerQualityDTO.BasicStats;
import org.cs209a.stackoverflowapp.entity.dto.AnswerQualityDTO.CorrelationStats;
import org.cs209a.stackoverflowapp.entity.dto.AnswerQualityDTO.Correlations;
import org.cs209a.stackoverflowapp.entity.dto.AnswerQualityDTO.GroupedStats;
import org.cs209a.stackoverflowapp.mapper.AnswerQualityDTOMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
public class AnswerQualityService {
    @Autowired
    private AnswerQualityDTOMapper answerQualityDTOMapper;

    @Transactional
    public AnswerQualityDTO analyze(AnswerAnalysisParams params) {
        validateParams(params);

        try {
            // 先删除可能存在的临时表
            answerQualityDTOMapper.dropTempTable();

            // 创建临时表
            answerQualityDTOMapper.createTempTable(params);

            AnswerQualityDTO result = new AnswerQualityDTO();

            // 获取基础统计和相关性分析
            Map<String, Object> statsData = answerQualityDTOMapper.getBasicStats();
            setBasicStats(result, statsData);
            setCorrelations(result, statsData);

            // 获取分组统计
            GroupedStats groupedStats = new GroupedStats();
            groupedStats.setTimeGapGroups(answerQualityDTOMapper.getTimeGapGroups(params));
            groupedStats.setReputationGroups(answerQualityDTOMapper.getReputationGroups(params));
            groupedStats.setLengthGroups(answerQualityDTOMapper.getLengthGroups(params));
            result.setGroupedStats(groupedStats);

            // 清理临时表
            answerQualityDTOMapper.dropTempTable();

            return result;
        } catch (Exception e) {
            // 确保发生异常时也清理临时表
            answerQualityDTOMapper.dropTempTable();
            throw new RuntimeException("Failed to analyze answer quality", e);
        }
    }

    private void validateParams(AnswerAnalysisParams params) {
        if (params == null) {
            throw new IllegalArgumentException("Analysis parameters cannot be null");
        }

        if (Math.abs(params.getScoreWeight() + params.getAcceptedWeight() - 1.0) > 0.0001) {
            throw new IllegalArgumentException("Score weight and accepted weight must sum to 1.0");
        }

        if (params.getMinGroupSize() < 1) {
            throw new IllegalArgumentException("Minimum group size must be positive");
        }

        if (params.getMaxResults() < 1) {
            throw new IllegalArgumentException("Maximum results must be positive");
        }
    }

    private void setBasicStats(AnswerQualityDTO result, Map<String, Object> statsData) {
        BasicStats basicStats = new BasicStats();
        basicStats.setTotalAnswers(((Number) statsData.get("total_answers")).intValue());
        basicStats.setAvgQualityScore(((Number) statsData.get("avg_quality_score")).doubleValue());
        basicStats.setAcceptedAnswersCount(((Number) statsData.get("accepted_answers_count")).intValue());
        result.setBasicStats(basicStats);
    }

    private void setCorrelations(AnswerQualityDTO result, Map<String, Object> statsData) {
        Correlations correlations = new Correlations();

        // 时间间隔相关性
        CorrelationStats timeGap = new CorrelationStats();
        timeGap.setCoefficient(((Number) statsData.get("time_gap_correlation")).doubleValue());
        timeGap.setPValue(calculatePValue(timeGap.getCoefficient(),
                ((Number) statsData.get("total_answers")).intValue()));

        // 用户声望相关性
        CorrelationStats reputation = new CorrelationStats();
        reputation.setCoefficient(((Number) statsData.get("reputation_correlation")).doubleValue());
        reputation.setPValue(calculatePValue(reputation.getCoefficient(),
                ((Number) statsData.get("total_answers")).intValue()));

        // 答案长度相关性
        CorrelationStats length = new CorrelationStats();
        length.setCoefficient(((Number) statsData.get("length_correlation")).doubleValue());
        length.setPValue(calculatePValue(length.getCoefficient(),
                ((Number) statsData.get("total_answers")).intValue()));

        correlations.setTimeGap(timeGap);
        correlations.setReputation(reputation);
        correlations.setLength(length);

        result.setCorrelations(correlations);
    }

    private double calculatePValue(double correlation, int sampleSize) {
        // 验证参数
        if (sampleSize <= 2) {
            throw new IllegalArgumentException("Sample size must be greater than 2");
        }
        if (Double.isNaN(correlation)) {
            return 1.0; // 如果相关系数是NaN，返回p值1.0表示完全不显著
        }

        try {
            // 计算t统计量
            double tStat = correlation * Math.sqrt((sampleSize - 2) / (1 - correlation * correlation));
            // 创建自由度为n-2的t分布
            TDistribution tDist = new TDistribution(sampleSize - 2);
//            // 计算双尾p值
            return 2 * (1 - tDist.cumulativeProbability(Math.abs(tStat)));

        } catch (Exception e) {
            return 1.0; // 出错时返回不显著
        }
    }


}
