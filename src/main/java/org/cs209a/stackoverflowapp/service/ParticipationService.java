package org.cs209a.stackoverflowapp.service;

import lombok.RequiredArgsConstructor;
import org.cs209a.stackoverflowapp.entity.dto.ParticipationDTO;
import org.cs209a.stackoverflowapp.mapper.ParticipationDTOMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ParticipationService {
    @Autowired
    private ParticipationDTOMapper participationMapper;

    public List<ParticipationDTO> getTopNParticipationTopics(int n, int R) {
        return participationMapper.getTopNParticipationTopics(n, R);
    }

}
