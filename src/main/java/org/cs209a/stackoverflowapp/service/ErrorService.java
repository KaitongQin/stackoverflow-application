package org.cs209a.stackoverflowapp.service;

import lombok.RequiredArgsConstructor;
import org.cs209a.stackoverflowapp.entity.dto.ErrorDTO;
import org.cs209a.stackoverflowapp.mapper.ErrorDTOMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ErrorService {

    @Autowired
    private ErrorDTOMapper errorDTOMapper;


    public List<ErrorDTO> getTopNJavaErrors(float weight, int n, String filter) {
        List<ErrorDTO> errors = errorDTOMapper.getTopNJavaTopics(weight);
        if (filter != null && !filter.isEmpty()) {
            errors = errors.stream()
                    .filter(error -> error.getErrorName().contains(filter) ||
                            error.getErrorType().equalsIgnoreCase(filter))
                    .limit(n)
                    .toList();
        }
        return errors;
    }
}
