package org.cs209a.stackoverflowapp.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("error")
public class Error {
    @TableId(type = IdType.AUTO)
    private String name;
    private String severity;
}