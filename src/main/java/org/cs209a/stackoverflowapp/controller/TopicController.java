package org.cs209a.stackoverflowapp.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.cs209a.stackoverflowapp.constant.PathConstant;
import org.cs209a.stackoverflowapp.entity.dto.TopicDTO;
import org.cs209a.stackoverflowapp.service.TopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

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
    public List<TopicDTO> getTopNJavaTopics(@RequestParam float weight,
                                            @RequestParam int n,
                                            @RequestParam(required = false) String filter) {
        return topicService.getTopNJavaTopics(weight, n,filter);
    }
}
