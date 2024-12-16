package org.cs209a.stackoverflowapp.service;

import lombok.RequiredArgsConstructor;
import org.cs209a.stackoverflowapp.entity.dto.ErrorDTO;
import org.cs209a.stackoverflowapp.mapper.ErrorDTOMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ErrorService {

    @Autowired
    private ErrorDTOMapper errorDTOMapper;


    public List<ErrorDTO> getTopNJavaErrors(float weight, int n, String filter) {
        List<ErrorDTO> errors = errorDTOMapper.getTopNJavaTopics(weight);
        if (filter != null && !filter.isEmpty()) {
            errors = errors.stream()
                    .filter(error -> error.getErrorName().contains(filter) || // 错误名包含filter
                            error.getErrorType().equalsIgnoreCase(filter)) // 错误类型等于filter（忽略大小写）
                    .toList();
        }
        return errors.stream()
                .limit(n)
                .toList();
    }

    public List<ErrorDTO> getSpecificJavaError(float weight, int n, String filter) {
        if (filter == null || filter.isEmpty()) {
            return List.of(); // 如果 filter 为空，返回空列表
        }
        return errorDTOMapper.getTopNJavaTopics(1) // 直接获取全部数据
                .stream()
                .filter(error -> error.getErrorName().equalsIgnoreCase(filter)) // 只保留名称完全匹配的项（忽略大小写）
                .toList();
    }
}
