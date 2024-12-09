package org.cs209a.stackoverflow.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Topic {
    private String topic;
    private Float weightedScore;
    private Float frequency;
}
