package org.cs209a.stackoverflow.model;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("error_occurrence")
public class ErrorOccurrence {
    private Integer id;
    private String errorType;
    private Integer postId;
    private String context;
}