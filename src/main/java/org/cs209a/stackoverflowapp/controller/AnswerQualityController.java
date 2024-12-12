package org.cs209a.stackoverflowapp.controller;

import lombok.RequiredArgsConstructor;
import org.cs209a.stackoverflowapp.constant.PathConstant;
import org.cs209a.stackoverflowapp.entity.dto.AnswerAnalysisParams;
import org.cs209a.stackoverflowapp.entity.dto.AnswerQualityDTO;
import org.cs209a.stackoverflowapp.service.AnswerQualityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping(PathConstant.API + "/answer-quality")
@RestController
@RequiredArgsConstructor
public class AnswerQualityController {
    //4. 答案质量（30分）
//我们认为一个答案是“高质量的”，如果它被采纳或获得了很多点赞。了解哪些因素会影响答案的质量是非常有价
//值的。
//请调查以下因素：
//问题创建和答案创建之间的时间间隔（例如，第一个发布的答案是否更容易被采纳？）。
//创建答案的用户的声望（例如，是否高声望用户的答案更容易被采纳或获得更多点赞？）。
//除了这两个因素，你还需要提出另一个可能对答案质量有贡献的因素。
//对于这三个因素，使用适当的数据分析和可视化，展示这些因素是否会影响答案质量。
    @Autowired
    private AnswerQualityService answerQualityService;

    @PostMapping
    public AnswerQualityDTO getTopNAnswerQuality(@RequestParam AnswerAnalysisParams params) {
        return answerQualityService.analyze(params);
    }

}
