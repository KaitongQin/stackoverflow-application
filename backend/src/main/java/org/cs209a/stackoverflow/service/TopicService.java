package org.cs209a.stackoverflow.service;


import lombok.RequiredArgsConstructor;
import org.cs209a.stackoverflow.mapper.QuestionMapper;
import org.cs209a.stackoverflow.model.Topic;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TopicService {
    private final QuestionMapper questionMapper;

    public List<Topic> getTopNJavaTopics(float weight, int n) {
        return questionMapper.getTopNJavaTopics(weight, n);
    }

    public List<Topic> getTopNParticipationTopics(int nInt, float w1Float, float w2Float, float w3Float, float w4Float) {
        return questionMapper.getTopNParticipationTopics(nInt, w1Float, w2Float, w3Float, w4Float);
    }
}
