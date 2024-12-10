package org.cs209a.stackoverflow.model;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("users")
public class User {
    @TableId
    private Integer accountId;
    private String profileImage;
    private String userType;
    private Integer userId;
    private String link;
    private String displayName;
    private Integer reputation;
}


