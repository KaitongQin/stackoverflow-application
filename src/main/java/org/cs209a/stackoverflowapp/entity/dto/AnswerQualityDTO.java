package org.cs209a.stackoverflowapp.entity.dto;

import lombok.Data;

import java.util.List;

@Data
public class AnswerQualityDTO {
    private BasicStats basicStats;
    private Correlations correlations;
    private GroupedStats groupedStats;

    @Data
    public static class BasicStats {
        private int totalAnswers;
        private double avgQualityScore;
        private int acceptedAnswersCount;
    }

    @Data
    public static class Correlations {
        private CorrelationStats timeGap;
        private CorrelationStats reputation;
        private CorrelationStats length;
    }

    @Data
    public static class CorrelationStats {
        private double coefficient;
        private double pValue;
    }

    @Data
    public static class GroupedStats {
        private List<GroupStats> timeGapGroups;
        private List<GroupStats> reputationGroups;
        private List<GroupStats> lengthGroups;
    }

    @Data
    public static class GroupStats {
        private float rangeStart;
        private float rangeEnd;
        private double avgQualityScore;
        private int count;
        private double acceptedRate;
    }
}
