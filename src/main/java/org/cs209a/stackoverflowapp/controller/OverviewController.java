package org.cs209a.stackoverflowapp.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.cs209a.stackoverflowapp.constant.PathConstant;
import org.cs209a.stackoverflowapp.entity.dto.OverviewDTO;
import org.cs209a.stackoverflowapp.entity.dto.TopicDTO;
import org.cs209a.stackoverflowapp.service.OverviewService;
import org.cs209a.stackoverflowapp.service.TopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(PathConstant.API + "/overview")
@RequiredArgsConstructor
public class OverviewController {
    @Autowired
    private OverviewService overviewService;

    @GetMapping
    public List<OverviewDTO> getOverview() {
        return overviewService.getOverview();
    }
}
