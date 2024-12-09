package org.cs209a.stackoverflow.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.cs209a.stackoverflow.common.constant.PathConstant;
import org.cs209a.stackoverflow.model.Topic;
import org.cs209a.stackoverflow.service.TopicService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(PathConstant.API + "topic")
@RequiredArgsConstructor
public class TopicController {


    private final TopicService topicService;

    //我们在这门课程中覆盖了各种话题，例如泛型、集合、I/O、Lambda、多线程、套接字等。了解在 Stack
    // Overflow 上最常见的 Java 相关话题会很有意思。
    //请回答以下问题：Stack Overflow 上最常被提及的前 N 个 Java 话题是什么？
    @GetMapping("/weight={weight}&n={n}/")
    public TopicList getTopNJavaTopics(@PathVariable String weight, @PathVariable String n) {
        try {
            float weightFloat = Float.parseFloat(weight);
            int nInt = Integer.parseInt(n);
            return new TopicList(topicService.getTopNJavaTopics(weightFloat, nInt));
        } catch (NumberFormatException ignore) {

        }
        return new TopicList(); // todo: 可以改成返回默认值
    }

    //在 Stack Overflow 上，用户参与度指的是任何用户活动（例如编辑、回答、评论、点赞、点踩等）在某个线程
    //中的表现。
    //请回答以下问题：Stack Overflow 上参与度最高的前 N 个 Java 话题是什么？其中“用户参与度”是指高声望用户
    //对该话题的参与。
    @GetMapping("/n={n}&w1={w1}&w2={w2}&w3={w3}&w4={w4}/")
    public TopicList getTopNParticipationTopics(@PathVariable String n, @PathVariable String w1,
                                                @PathVariable String w2, @PathVariable String w3,
                                                @PathVariable String w4) {
        try {
            int nInt = Integer.parseInt(n);
            float w1Float = Float.parseFloat(w1);
            float w2Float = Float.parseFloat(w2);
            float w3Float = Float.parseFloat(w3);
            float w4Float = Float.parseFloat(w4);
            return new TopicList(topicService.getTopNParticipationTopics(nInt, w1Float, w2Float, w3Float, w4Float));
        } catch (NumberFormatException ignore) {
        }
        return new TopicList(); //  todo: 可以改成返回默认值
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TopicList {
        private List<Topic> topicList;
    }
}
