package org.cs209a.stackoverflow.model;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("question_tag")
public class QuestionTag {
    private Integer questionId;
    private String tagName;
}