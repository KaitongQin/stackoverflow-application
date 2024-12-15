package org.cs209a.stackoverflowapp.service;

import lombok.RequiredArgsConstructor;
import org.cs209a.stackoverflowapp.entity.dto.OverviewDTO;
import org.cs209a.stackoverflowapp.mapper.OverviewDTOMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OverviewService {

    @Autowired
    private OverviewDTOMapper overviewDTOMapper;

    public List<OverviewDTO> getOverview() {
        return overviewDTOMapper.getOverview();
    }
}
