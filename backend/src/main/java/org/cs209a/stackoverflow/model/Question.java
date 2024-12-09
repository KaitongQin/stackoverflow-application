package org.cs209a.stackoverflow.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.sql.Timestamp;

@Data
@TableName("questions")
public class Question {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer ownerId;
    private String title;
    private String body;
    private Timestamp creationDate;
    private Integer answerCount;
    private Integer commentCount;
    private Integer viewCount;
    private Integer score;
}
