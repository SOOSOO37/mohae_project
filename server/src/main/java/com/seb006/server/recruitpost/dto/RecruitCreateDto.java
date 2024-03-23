package com.seb006.server.recruitpost.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RecruitCreateDto {

    private Long prfPostId;

    private String title;

    private String category;

    private String content;

    private int recruitNumber;

    private String dueDate;

    private String age;

    private String tags;


}
