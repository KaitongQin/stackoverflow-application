package org.cs209a.stackoverflowapp.controller;

import lombok.RequiredArgsConstructor;
import org.apache.ibatis.annotations.Param;
import org.cs209a.stackoverflowapp.constant.PathConstant;
import org.cs209a.stackoverflowapp.entity.Topic;
import org.cs209a.stackoverflowapp.service.TopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping(PathConstant.API + "/topic")
@RequiredArgsConstructor
public class TopicController {

    @Autowired
    private TopicService topicService;

    //我们在这门课程中覆盖了各种话题，例如泛型、集合、I/O、Lambda、多线程、套接字等。了解在 Stack
    // Overflow 上最常见的 Java 相关话题会很有意思。
    //请回答以下问题：Stack Overflow 上最常被提及的前 N 个 Java 话题是什么？
    @GetMapping
    public TopicList getTopNJavaTopics(@RequestParam float weight, @RequestParam int n) {
        return new TopicList(topicService.getTopNJavaTopics(weight, n));
    }

    //在 Stack Overflow 上，用户参与度指的是任何用户活动（例如编辑、回答、评论、点赞、点踩等）在某个线程
    //中的表现。
    //请回答以下问题：Stack Overflow 上参与度最高的前 N 个 Java 话题是什么？其中“用户参与度”是指高声望用户
    //对该话题的参与。
    @GetMapping("/participation")
    TopicList getTopNParticipationTopics(
            @RequestParam int n,
            @RequestParam float w1,
            @RequestParam float w2,
            @RequestParam float w3,
            @RequestParam float w4
    ) {
        return new TopicList(topicService.getTopNParticipationTopics(n, w1, w2, w3, w4));
    }


    public static class TopicList {
        private List<Topic> topicList = new ArrayList<>();

        public TopicList(List<Topic> topicList) {
            this.topicList = topicList;
        }

        public TopicList() {
            this.topicList = new ArrayList<>();
        }

        public List<Topic> getTopicList() {
            return this.topicList;
        }

        public void setTopicList(List<Topic> topicList) {
            this.topicList = topicList;
        }

        public boolean equals(final Object o) {
            if (o == this) return true;
            if (!(o instanceof TopicList other)) return false;
            if (!other.canEqual(this)) return false;
            final Object this$topicList = this.getTopicList();
            final Object other$topicList = other.getTopicList();
            return Objects.equals(this$topicList, other$topicList);
        }

        protected boolean canEqual(final Object other) {
            return other instanceof TopicList;
        }

        public int hashCode() {
            final int PRIME = 59;
            int result = 1;
            final Object $topicList = this.getTopicList();
            result = result * PRIME + ($topicList == null ? 43 : $topicList.hashCode());
            return result;
        }

        public String toString() {
            return "TopicController.TopicList(topicList=" + this.getTopicList() + ")";
        }
    }
}
