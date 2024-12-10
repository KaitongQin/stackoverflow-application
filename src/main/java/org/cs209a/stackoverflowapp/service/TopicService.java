package org.cs209a.stackoverflowapp.service;


import lombok.RequiredArgsConstructor;
import org.cs209a.stackoverflowapp.mapper.QuestionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.cs209a.stackoverflowapp.entity.Topic;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TopicService {
    @Autowired
    private QuestionMapper questionMapper;

    public List<Topic> getTopNJavaTopics(float weight, int n) {
        return questionMapper.getTopNJavaTopics(weight, n);
    }

    public List<Topic> getTopNParticipationTopics(int n, float w1, float w2, float w3, float w4) {
        List<Topic> result = questionMapper.getTopNParticipationTopics(n, w1, w2, w3, w4);
        return result
                .stream()
                .filter(topic -> topic.getRelatedQuestionNum() != null)
                .sorted(Comparator.comparing(Topic::getRelatedQuestionNum).reversed())
                .limit(n + 1)
                .toList()
                .subList(1, n + 1);
    }
}
