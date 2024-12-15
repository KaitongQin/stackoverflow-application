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
}
