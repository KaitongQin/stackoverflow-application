package org.cs209a.stackoverflowapp.service;


import lombok.RequiredArgsConstructor;
import org.cs209a.stackoverflowapp.entity.dto.ErrorDTO;
import org.cs209a.stackoverflowapp.entity.dto.TopicDTO;
import org.cs209a.stackoverflowapp.mapper.TopicDTOMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TopicService {
    @Autowired
    private TopicDTOMapper topicDTOMapper;

    public List<TopicDTO> getTopNJavaTopics(float weight, int n, String filter) {
        List<TopicDTO> topics = topicDTOMapper.getTopNJavaTopics(weight);
        if (filter != null && !filter.isEmpty()) {
            topics = topics.stream()
                    .filter(topic -> topic.getTagName().contains(filter) ||
                            topic.getTagName().equalsIgnoreCase(filter))
                    .limit(n)
                    .toList();
        }
        return topics.stream()
                .limit(n)
                .toList();
    }
}
