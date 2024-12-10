package org.cs209a.stackoverflowapp.controller;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.cs209a.stackoverflowapp.constant.PathConstant;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(PathConstant.API + "/error")
@RequiredArgsConstructor
public class ErrorController {
//    #### 3. 常见错误（15分）
//开发人员在编码过程中常常会犯错误，导致代码中的 bug。这些错误表现为错误或异常，通常可以分为两类：
//- **致命错误**：如 `OutOfMemoryError`，在运行时无法恢复。
//- **异常**：包括已检查异常和运行时异常，开发人员可以在程序中进行处理。
//请回答以下问题：在 Java 开发者中，最常讨论的前 N 种错误和异常是什么？
//注意：标签是高层次的分类，可能无法涵盖低层次的错误或异常。因此，除了标签信息之外，你还需要进一步分析线程内容（例如问题文本和答案文本），可能需要使用正则表达式匹配等高级技术来识别错误或异常相关的信息。
//```
//错误频率 = 包含该错误的问题数 / 总问题数
//错误影响度 = 包含该错误的问题的平均浏览量
//错误严重度 = 错误频率 * (1 + w1 * log(错误影响度))

    public ErrorList getTopNJavaErrors(@RequestParam float weight, @RequestParam int n) {
        return new ErrorList();
    }

    @Data
    public static class ErrorList {
        List<Error> errorList;
    }
}
