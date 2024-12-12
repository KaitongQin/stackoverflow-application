package org.cs209a.stackoverflowapp.service;


import lombok.RequiredArgsConstructor;
import org.cs209a.stackoverflowapp.controller.TopicController;
import org.cs209a.stackoverflowapp.entity.dto.TopicDTO;
import org.cs209a.stackoverflowapp.mapper.QuestionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TopicService {
    @Autowired
    private QuestionMapper questionMapper;

    public List<TopicDTO> getTopNJavaTopics(float weight, int n) {
        return questionMapper.getTopNJavaTopics(weight, n);
    }
}
