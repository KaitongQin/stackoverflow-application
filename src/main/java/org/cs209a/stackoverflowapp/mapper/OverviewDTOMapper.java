package org.cs209a.stackoverflowapp.mapper;

import org.apache.ibatis.annotations.Select;
import org.cs209a.stackoverflowapp.entity.dto.OverviewDTO;

import java.util.List;

public interface OverviewDTOMapper {
    @Select("""
    SELECT
        (SELECT COUNT(*) FROM question) AS question_num,
        (SELECT COUNT(*) FROM answer) AS answer_num,
        (SELECT COUNT(*) FROM comment) AS comment_num,
        (SELECT COUNT(*) FROM "user") AS user_num,
        (SELECT COUNT(DISTINCT tag_name) FROM question_tag) AS tag_num,
        (SELECT COUNT(*) FROM error) AS error_num
    """)
    List<OverviewDTO> getOverview();
}
