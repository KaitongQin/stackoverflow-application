package org.cs209a.stackoverflowapp.entity.dto;

import lombok.Data;

@Data
public class ParticipationDTO {
    private String tagName; // 话题名称
    private int answerNum;
    private int commentNum;
    private int revisionNum;
    private int voteNum;
    private int engagementScore;
    private String highReputationTagName;
    private int highReputationAnswerNum;
    private int highReputationCommentNum;
    private int highReputationRevisionNum;
    private int highReputationVoteNum;
    private int highReputationEngagementScore;
}
