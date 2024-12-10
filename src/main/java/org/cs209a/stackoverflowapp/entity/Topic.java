package org.cs209a.stackoverflowapp.entity;

public class Topic {
    private String topic;
    private Float relatedQuestionNum;
    private Float frequency;

    public Topic(String topic, Float relatedQuestionNum, Float frequency) {
        this.topic = topic;
        this.relatedQuestionNum = relatedQuestionNum;
        this.frequency = frequency;
    }

    public Topic() {
    }

    public String getTopic() {
        return this.topic;
    }

    public Float getRelatedQuestionNum() {
        return this.relatedQuestionNum;
    }

    public Float getFrequency() {
        return this.frequency;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public void setRelatedQuestionNum(Float relatedQuestionNum) {
        this.relatedQuestionNum = relatedQuestionNum;
    }

    public void setFrequency(Float frequency) {
        this.frequency = frequency;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof Topic)) return false;
        final Topic other = (Topic) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$topic = this.getTopic();
        final Object other$topic = other.getTopic();
        if (this$topic == null ? other$topic != null : !this$topic.equals(other$topic)) return false;
        final Object this$weightedScore = this.getRelatedQuestionNum();
        final Object other$weightedScore = other.getRelatedQuestionNum();
        if (this$weightedScore == null ? other$weightedScore != null : !this$weightedScore.equals(other$weightedScore))
            return false;
        final Object this$frequency = this.getFrequency();
        final Object other$frequency = other.getFrequency();
        if (this$frequency == null ? other$frequency != null : !this$frequency.equals(other$frequency)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof Topic;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $topic = this.getTopic();
        result = result * PRIME + ($topic == null ? 43 : $topic.hashCode());
        final Object $weightedScore = this.getRelatedQuestionNum();
        result = result * PRIME + ($weightedScore == null ? 43 : $weightedScore.hashCode());
        final Object $frequency = this.getFrequency();
        result = result * PRIME + ($frequency == null ? 43 : $frequency.hashCode());
        return result;
    }

    public String toString() {
        return "Topic(topic=" + this.getTopic() + ", weightedScore=" + this.getRelatedQuestionNum() + ", frequency=" + this.getFrequency() + ")";
    }
}
