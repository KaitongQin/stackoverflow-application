package org.cs209a.stackoverflowapp.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.sql.Timestamp;
@Data
@TableName("comment")
public class Comment {
    private Integer id;
    private Integer postId;
    private String body;
    private Integer score;
    private Integer ownerId;
    private Timestamp creationDate;
    private Boolean edited;
}
