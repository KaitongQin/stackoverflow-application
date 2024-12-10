package org.cs209a.stackoverflowapp.controller;

import lombok.RequiredArgsConstructor;
import org.cs209a.stackoverflowapp.constant.PathConstant;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
//质量分数 = w1 * 点赞数 + w2 * (是否被采纳 ? 1 : 0)
//响应速度分数 = 1 - min(回答时间间隔 / 24小时, 1) # 24小时内响应获得满分
//声望权重 = log(1 + 用户声望值/1000) # 使用对数避免声望差距过大
    public AnswerQualityList getTopNAnswerQuality(@RequestParam float weight, @RequestParam int n) {
        return new AnswerQualityList();
    }

    public static class AnswerQualityList {
        List<AnswerQuality> answerQualityList;
    }

    public static class AnswerQuality {
        private float qualityScore;
        private float responseSpeedScore;
        private float reputationWeight;
        private float score;
    }
}
