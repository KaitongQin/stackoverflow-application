package org.cs209a.stackoverflow.model;


import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.sql.Timestamp;

@Data
@TableName("answer")
public class Answer {
    private Integer id;
    private Integer questionId;
    private String body;
    private Integer score;
    private Boolean isAccepted;
    private Integer ownerId;
    private Timestamp creationDate;
}
