package org.cs209a.stackoverflow.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.cs209a.stackoverflow.model.User;

import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    void insertBatch(@Param("user")List<User> userList);
}

