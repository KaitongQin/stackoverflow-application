package org.cs209a.stackoverflowapp.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("error_occurrence")
public class ErrorOccurrence {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String errorType;
    private Integer postId;
    private String context;
}