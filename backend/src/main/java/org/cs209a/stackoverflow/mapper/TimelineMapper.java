package org.cs209a.stackoverflow.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.cs209a.stackoverflow.model.Timeline;
@Mapper
public interface TimelineMapper extends BaseMapper<Timeline> {
}
