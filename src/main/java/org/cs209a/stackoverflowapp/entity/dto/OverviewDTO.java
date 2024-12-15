package org.cs209a.stackoverflowapp.entity.dto;

import lombok.Data;

@Data
public class OverviewDTO {
    private int questionNum;
    private int answerNum;
    private int commentNum;
    private int userNum;
    private int tagNum;
    private int errorNum;
}
