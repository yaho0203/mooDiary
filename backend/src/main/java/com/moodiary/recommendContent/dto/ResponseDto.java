package com.moodiary.recommendContent.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ResponseDto {
    private String imageUrl;
    private String title;
    private String content;
    private Long contentId;
}
