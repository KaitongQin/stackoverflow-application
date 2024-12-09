package org.cs209a.stackoverflow.model;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("error")
public class Error {
    private String name;
    private String severity;
}