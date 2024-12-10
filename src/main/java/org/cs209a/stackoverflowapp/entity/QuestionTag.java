package org.cs209a.stackoverflowapp.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("question_tag")
public class QuestionTag {
    private Integer questionId;
    private String tagName;
}