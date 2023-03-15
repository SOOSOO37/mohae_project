package com.seb006.server.recruitpost.dto;

import com.seb006.server.recruitpost.entity.RecruitPost;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class RecruitPostPatchDto {

    private Long id;

    private String title;

    private String category;

    private String content;

    private int recruitNumber;

    private RecruitPost.RecruitStatus recruitStatus;

    private LocalDate dueDate;

    private String age;

    private String tags;
}
