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

    public List<ParticipationDTO> getTopNParticipationTopics(int n, float w1, float w2, float w3, float w4) {
        return participationMapper.getTopNParticipationTopics(n, w1, w2, w3, w4);
    }

}
