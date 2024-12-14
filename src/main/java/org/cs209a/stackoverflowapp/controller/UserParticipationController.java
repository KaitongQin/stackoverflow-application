package org.cs209a.stackoverflowapp.controller;

import lombok.RequiredArgsConstructor;
import org.cs209a.stackoverflowapp.constant.PathConstant;
import org.cs209a.stackoverflowapp.entity.dto.ParticipationDTO;
import org.cs209a.stackoverflowapp.service.ParticipationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(PathConstant.API + "/participation")
@RequiredArgsConstructor
public class UserParticipationController {
    //在 Stack Overflow 上，用户参与度指的是任何用户活动（例如编辑、回答、评论、点赞、点踩等）在某个线程
    //中的表现。
    //请回答以下问题：Stack Overflow 上参与度最高的前 N 个 Java 话题是什么？其中“用户参与度”是指高声望用户
    //对该话题的参与。
    private final ParticipationService participationService;

    @GetMapping
    List<ParticipationDTO> getTopNParticipationTopics(
            @RequestParam int n,
            @RequestParam int R
    ) {
        return participationService.getTopNParticipationTopics(n, R);
    }
}
