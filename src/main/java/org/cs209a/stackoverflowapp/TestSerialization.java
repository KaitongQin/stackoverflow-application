package org.cs209a.stackoverflowapp;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.cs209a.stackoverflowapp.controller.TopicController;
import org.cs209a.stackoverflowapp.entity.Topic;

import java.util.Arrays;

public class TestSerialization {
//    public static void main(String[] args) throws Exception {
//        Topic topic1 = new Topic();
//        topic1.setTopic("Java");
//        topic1.setFrequency(95.5f);
//        topic1.setWeightedScore(120f);
//
//        Topic topic2 = new Topic();
//        topic2.setTopic("Spring");
//        topic2.setWeightedScore(88.0f);
//        topic2.setFrequency(85f);
//
//        TopicController.TopicList topicList = new TopicController.TopicList(Arrays.asList(topic1, topic2));
//
//        // 使用 Jackson 序列化
//        ObjectMapper mapper = new ObjectMapper();
//        String json = mapper.writeValueAsString(topicList);
//        System.out.println(json);
//    }
}
