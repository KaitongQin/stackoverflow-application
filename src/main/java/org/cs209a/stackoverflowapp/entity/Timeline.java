package org.cs209a.stackoverflowapp.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.sql.Timestamp;

@TableName("timeline")
@Data
public class Timeline {
    private Integer commentId;
    private Timestamp creationDate;
    private Integer downVoteCount;
    private Integer ownerId;
    private Integer postId;
    private Integer questionId;
    private String revisionGuid;
    private String timelineType;
    private Integer upVoteCount;
    private Integer userId;
}
