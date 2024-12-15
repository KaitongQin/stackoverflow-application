package org.cs209a.stackoverflowapp.controller;

import lombok.RequiredArgsConstructor;
import org.cs209a.stackoverflowapp.constant.PathConstant;
import org.cs209a.stackoverflowapp.entity.dto.ErrorDTO;
import org.cs209a.stackoverflowapp.service.ErrorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(PathConstant.API + "/error")
@RequiredArgsConstructor
public class ErrorController {

    @Autowired
    private ErrorService errorService;

    // 原有方法：获取最常见的 Top N 错误或异常
    @GetMapping
    public List<ErrorDTO> getTopNJavaErrors(@RequestParam float weight,
                                            @RequestParam int n,
                                            @RequestParam(required = false) String filter) {
        if (weight < 0 || n <= 0) {
            return null;
        }
        return errorService.getTopNJavaErrors(weight, n, filter);
    }

    // 新方法：获取指定名称的错误
    @GetMapping("/specific")
    public List<ErrorDTO> getSpecificJavaError(@RequestParam float weight,
                                               @RequestParam int n,
                                               @RequestParam String filter) {
        if (weight < 0 || n <= 0 || filter == null || filter.isEmpty()) {
            return List.of();
        }
        return errorService.getSpecificJavaError(weight, n, filter);
    }
}
